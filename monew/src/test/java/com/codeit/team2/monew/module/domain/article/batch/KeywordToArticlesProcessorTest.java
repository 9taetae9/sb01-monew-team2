package com.codeit.team2.monew.module.domain.article.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.NaverNewsClient;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import java.time.Instant;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KeywordToArticlesProcessorTest {

    @Mock
    private NaverNewsClient naverNewsClient;

    private KeywordToArticlesProcessor processor;

    @BeforeEach
    void setup() {
        processor = new KeywordToArticlesProcessor(naverNewsClient);
    }

    @Test
    void processor_shouldReturnArticle_whenKeywordIsGiven() throws Exception {
        //given
        Keyword keyword = new Keyword("AI");
        List<Article> articles = List.of(
            new Article("AI", "NAVER", "https://test.com", "this is test summary", 0,
                Instant.now(), false));

        BDDMockito.given(naverNewsClient.fetchArticles(any()))
            .willReturn(articles);

        // when

        List<Article> result = processor.process(keyword);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("AI");
        BDDMockito.then(naverNewsClient).should(times(1)).fetchArticles(any());
    }
}
