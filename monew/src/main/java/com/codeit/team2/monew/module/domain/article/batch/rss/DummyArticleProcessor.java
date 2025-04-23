package com.codeit.team2.monew.module.domain.article.batch.rss;

import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class DummyArticleProcessor implements
    ItemProcessor<DummyArticle, List<ArticleInterestCreateCommand>> {

    private final KeywordCache keywordCache;
    private final ArticleMapper articleMapper;

    @Override
    public List<ArticleInterestCreateCommand> process(DummyArticle item) throws Exception {

        Map<String, List<Interest>> keywordMap = keywordCache.getCache();

        // TODO : 지금 O(N * K) 시간 복잡도.. 개선 필요
        return keywordMap.entrySet().stream()
            .filter(entry -> item.getTitle().contains(entry.getKey()) || item.getSummary().contains(
                entry.getKey()))
            .flatMap(entry -> entry.getValue().stream()
                .map(interest ->
                    new ArticleInterestCreateCommand(articleMapper.dummyArticleToArticle(item),
                        interest)
                )
            ).toList();
    }
}
