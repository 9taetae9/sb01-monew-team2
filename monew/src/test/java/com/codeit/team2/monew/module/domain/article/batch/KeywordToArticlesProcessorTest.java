package com.codeit.team2.monew.module.domain.article.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.NaverApiNewsClient;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.repository.InterestKeywordRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KeywordToArticlesProcessorTest {

    @Mock
    private NaverApiNewsClient naverNewsClient;

    @Mock
    private InterestKeywordRepository interestKeywordRepository;

    private KeywordToArticlesProcessor processor;

    @BeforeEach
    void setup() {
        //processor = new KeywordToArticlesProcessor(naverNewsClient, interestKeywordRepository);
    }

    @Test
    void processor_shouldReturnArticle_whenKeywordIsGiven() throws Exception {
        //given
        Keyword keyword = new Keyword("AI");

        Article article = new Article("AI", "NAVER", "https://test.com", "this is test summary",
            Collections.emptySet(), 0L,
            Instant.now(), false);
        Interest interest = Interest.create("a");
        List<Article> articles = List.of(article);

        InterestKeyword ik = new InterestKeyword(interest, keyword);
        given(naverNewsClient.fetchArticles(any()))
            .willReturn(articles);
        given(interestKeywordRepository.findAllByKeyword(any()))
            .willReturn(List.of(ik));

        // when

        List<ArticleInterestCreateCommand> result = processor.process(keyword);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).article().getTitle()).isEqualTo("AI");
        then(naverNewsClient).should(times(1)).fetchArticles(any());

    }
}
