package com.codeit.team2.monew.module.domain.article.batch.rss;

import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapperImpl;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DummyArticleProcessorTest {

    private KeywordCache keywordCache;
    private ArticleMapper articleMapper;

    private DummyArticleProcessor processor;

    @BeforeEach
    void setUp() {
        keywordCache = Mockito.mock(KeywordCache.class);
        articleMapper = new ArticleMapperImpl();

        processor = new DummyArticleProcessor(keywordCache, articleMapper);
    }

    @Test
    void processorShouldReturnCommand() throws Exception {
        // given
        DummyArticle dummy = new DummyArticle(UUID.randomUUID(), "test", "TEST", "http://test.com",
            "this is test summary", 0L, Instant.now(), false);
        Article article = articleMapper.dummyArticleToArticle(dummy);
        Keyword keyword = new Keyword("test");
        Interest interest = Interest.create("test");
        interest.addInterestKeyword(keyword);

        BDDMockito.given(keywordCache.getCache())
            .willReturn(Map.of("test", List.of(interest)));

        // when
        List<ArticleInterestCreateCommand> result = processor.process(dummy);

        // then
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0).article().getSource()).isEqualTo("TEST");
    }
}
