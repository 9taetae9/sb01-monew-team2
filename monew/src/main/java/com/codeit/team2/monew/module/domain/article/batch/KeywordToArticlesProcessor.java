package com.codeit.team2.monew.module.domain.article.batch;


import com.codeit.team2.monew.module.domain.article.batch.rss.KeywordCache;
import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.NaverApiNewsClient;
import com.codeit.team2.monew.module.domain.article.repository.ArticleInterestRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.repository.InterestKeywordRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@StepScope
public class KeywordToArticlesProcessor implements
    ItemProcessor<Keyword, List<ArticleInterestCreateCommand>> {

    private final NaverApiNewsClient naverNewsClient;
    private final InterestKeywordRepository interestKeywordRepository;
    private final ArticleInterestRepository articleInterestRepository;
    private final KeywordCache keywordCache;
//    private final ArticleRepository articleRepository;

    @Override
    public List<ArticleInterestCreateCommand> process(Keyword keyword) throws Exception {

        // TODO : keyword 별 마지막 article? 조회하여 시간 비교 후 일찍 끝내기
        // TODO : 하나의 keyword 당 여러 api 호출이 발생 -> 효율적인 방법 고민
        // List<InterestKeyword> iks = interestKeywordRepository.findAllByKeyword(keyword);
        List<Interest> interests = keywordCache.getCache()
            .get(keyword.getName()); //iks.stream().map(ik -> ik.getInterest()) // TODO : N + 1
        // .collect(Collectors.toList());

        List<Article> articles = new ArrayList<>();

        Instant leastRecentTime = articleInterestRepository.fetchLeastRecentCreatedAtInInterests(
            interests).stream().min(Instant::compareTo).orElse(Instant.EPOCH);

        for (int start = 1; start <= 1000; start += 100) {
            FetchCommand cmd = new FetchCommand(keyword.getName(), 100, start, "date");
            List<Article> part = naverNewsClient.fetchArticles(cmd);
            articles.addAll(part);

            if (part.isEmpty()) {
                break;
            }

            if (part.size() < 100) {
                break;
            }

            if (part.get(part.size() - 1).getPublishedDate().isBefore(leastRecentTime)) {
                break;
            }
        }

        return interests.stream()
            .flatMap(i -> articles.stream().map(a -> new ArticleInterestCreateCommand(a, i)))
            .toList();
    }
}
