package com.codeit.team2.monew.module.domain.article.batch;

import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.notification.service.NotificationService;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchArticleWriter implements ItemWriter<List<ArticleInterestCreateCommand>> {

    private final ArticleRepository articleRepository;
    private final JdbcTemplate jdbcTemplate;
    private final NotificationService notificationService;

    @Override
    public void write(Chunk<? extends List<ArticleInterestCreateCommand>> items) throws Exception {

        List<ArticleInterestCreateCommand> allCmd = flattenChunk(items);

        // 기사 링크 수집 + mapping
        Set<String> links = new HashSet<>();
        Map<String, Article> linkToArticle = new HashMap<>(); // 캐시 역할

        for (ArticleInterestCreateCommand cmd : allCmd) {
            links.add(cmd.article().getSourceUrl());
            linkToArticle.putIfAbsent(cmd.article().getSourceUrl(), cmd.article());
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
        articleRepository.flush();
        // UPDATE & INSERT
        List<ArticleInterest> articleInterests = new ArrayList<>();

        for (ArticleInterestCreateCommand cmd : allCmd) {
            Article article = existing.get(cmd.article().getSourceUrl()); // 영속화 보장
            Interest interest = cmd.interest(); // 이전 단계에서 영속화
            articleInterests.add(new ArticleInterest(article, interest));
        }

        String sql = """
              INSERT INTO article_interests (id, article_id, interest_id, created_at)
              VALUES (?, ?, ?, ?)
              ON CONFLICT (article_id, interest_id) DO NOTHING
            """;
        jdbcTemplate.batchUpdate(sql, articleInterests, 1000, (ps, ai) -> {
            UUID id = UUID.randomUUID();
            Instant now = Instant.now();
            ps.setObject(1, id);
            ps.setObject(2, ai.getArticle().getId());
            ps.setObject(3, ai.getInterest().getId());
            ps.setObject(4, Timestamp.from(now));
        });

        // 알림 생성
        Set<UUID> newlySavedArticleIds = toSave.stream()
            .map(Article::getId)
            .collect(Collectors.toSet());
        // 새롭게 등록된 기사(toSave)만 필터링
        List<ArticleInterest> newlyCreatedInterests = articleInterests.stream()
            .filter(ai -> newlySavedArticleIds.contains(ai.getArticle().getId()))
            .toList();

        if (!newlyCreatedInterests.isEmpty()) {
            notificationService.createArticleInterestNotification(newlyCreatedInterests);
        }
    }

    private List<ArticleInterestCreateCommand> flattenChunk(
        Chunk<? extends List<ArticleInterestCreateCommand>> items) {
        List<ArticleInterestCreateCommand> flatList = new ArrayList<>();
        for (List<ArticleInterestCreateCommand> chunk : items) {
            flatList.addAll(chunk);
        }

        return flatList;
    }
}
