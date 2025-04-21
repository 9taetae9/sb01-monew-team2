package com.codeit.team2.monew.module.domain.article.batch;

import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleInterestRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// TODO : ARTICLE_INTEREST 에도 INSERT?
public class BatchArticleWriter implements ItemWriter<List<ArticleInterestCreateCommand>> {

    private final ArticleRepository articleRepository;
    private final ArticleInterestRepository articleInterestRepository;

    @Override
    public void write(Chunk<? extends List<ArticleInterestCreateCommand>> items) throws Exception {

        // 기사 링크 수집 + mapping
        Set<String> links = new HashSet<>();
        Map<String, Article> linkToArticle = new HashMap<>(); // 캐시 역할
        for (List<ArticleInterestCreateCommand> cmds : items) {
            for (ArticleInterestCreateCommand cmd : cmds) {
                links.add(cmd.article().getSourceUrl());
                linkToArticle.putIfAbsent(cmd.article().getSourceUrl(), cmd.article());
            }
        }

        // 이미 존재하는 기사 조회
        Map<String, Article> existing = articleRepository.findAllBySourceUrlIn(links)
            .stream().collect(Collectors.toMap(Article::getSourceUrl, a -> a));

        // 존재하지 않는 기사 추출
        List<Article> toSave = new ArrayList<>();
        for (String link : links) {
            if (!existing.containsKey(link)) {
                toSave.add(linkToArticle.get(link));
            }
        }

        // 기사 저장 및 mapping
        articleRepository.saveAll(toSave).forEach(a -> existing.put(a.getSourceUrl(), a));

        // UPDATE & INSERT
        Set<ArticleInterest> articleInterests = new HashSet<>();
        for (List<ArticleInterestCreateCommand> cmds : items) {
            for (ArticleInterestCreateCommand cmd : cmds) {
                Article article = existing.get(cmd.article().getSourceUrl());
                Interest interest = cmd.interest();

                // TODO : 조회 쿼리 너무 많이 발생. 최적화 필요
                if (!articleInterestRepository.existsByArticleAndInterest(article, interest)) {
                    articleInterests.add(new ArticleInterest(article, interest));
                }
            }
        }
        // TODO : JdbcTemplate 사용 고려

        // IGNORE ON CONFLICT, ArticleInterest 전부 적재하고 메모리상에서 검사
        articleInterestRepository.saveAll(articleInterests);
    }
}
