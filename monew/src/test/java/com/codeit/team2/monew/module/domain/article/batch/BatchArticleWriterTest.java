package com.codeit.team2.monew.module.domain.article.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleInterestRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import java.time.Instant;
import java.util.Collections;

import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;

@ExtendWith(MockitoExtension.class)
public class BatchArticleWriterTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleInterestRepository articleInterestRepository;
    private BatchArticleWriter writer;

    @BeforeEach
    void setup() {
        writer = new BatchArticleWriter(articleRepository, articleInterestRepository);
    }


    @Test
    void testWriteToRepository_success() throws Exception {
        //given


        Article a = new Article("a", "a", "a", "a", Collections.emptySet(), 0, Instant.now(),
            false);
        Article b = new Article("b", "b", "b", "b", Collections.emptySet(), 0, Instant.now(),
            false);
        Interest i1 = Interest.create("a");
        Interest i2 = Interest.create("b");

        List<ArticleInterestCreateCommand> cmd1 = List.of(
            new ArticleInterestCreateCommand(
                a,
                i1
            )
        );

        List<ArticleInterestCreateCommand> cmd2 = List.of(
            new ArticleInterestCreateCommand(
                b,
                i2
            )
        );

        Chunk<List<ArticleInterestCreateCommand>> combined = new Chunk(List.of(cmd1, cmd2));

        given(articleRepository.findAllBySourceUrlIn(any()))
            .willReturn(List.of());
        given(articleRepository.saveAll(any()))
            .willReturn(List.of(a, b));
        given(articleInterestRepository.existsByArticleAndInterest(any(), any()))
            .willReturn(false);

        given(articleInterestRepository.saveAll(any()))
            .willReturn(Collections.emptyList());

        //when
        writer.write(combined);

        // then
        ArgumentCaptor<List<Article>> captor = ArgumentCaptor.forClass(List.class);

        then(articleRepository).should().saveAll(captor.capture());

        List<Article> flatList = captor.getValue();
        Assertions.assertThat(flatList).hasSize(2);

    }
}
