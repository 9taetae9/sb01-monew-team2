package com.codeit.team2.monew.module.domain.article.batch.rss;

import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class DummyArticleProcessorV2 implements
    ItemProcessor<DummyArticle, List<ArticleInterestCreateCommand>> {

    private final KeywordCache keywordCache;
    private final ArticleMapper articleMapper;

    @Override
    public List<ArticleInterestCreateCommand> process(DummyArticle item) {
        // article 하나당 관련 keyword -> interest 매핑
        return keywordCache.getCache().entrySet().stream()
            .filter(entry ->
                item.getTitle().contains(entry.getKey()) ||
                    (item.getSummary() != null && item.getSummary().contains(entry.getKey())))
            .flatMap(entry -> entry.getValue().stream()
                .map(interest -> new ArticleInterestCreateCommand(
                    articleMapper.dummyArticleToArticle(item), interest)))
            .toList();
    }
}
