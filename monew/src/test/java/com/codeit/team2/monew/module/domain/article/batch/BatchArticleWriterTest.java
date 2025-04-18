package com.codeit.team2.monew.module.domain.article.batch;

import static org.mockito.BDDMockito.then;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import java.time.Instant;
import java.util.List;
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

    private BatchArticleWriter writer;

    @BeforeEach
    void setup() {
        writer = new BatchArticleWriter(articleRepository);
    }


    @Test
    void testWriteToRepository_success() throws Exception {
        //given
        List<Article> articles1 = List.of(
            new Article("a", "a", "a", "a", 0, Instant.now(), false),
            new Article("b", "b", "b", "b", 0, Instant.now(), false)
        );

        List<Article> articles2 = List.of(
            new Article("c", "c", "c", "c", 0, Instant.now(), false),
            new Article("d", "d", "d", "d", 0, Instant.now(), false)
        );

        Chunk<List<Article>> combined = new Chunk(List.of(articles1, articles2));

        //when
        writer.write(combined);

        // then
        ArgumentCaptor<List<Article>> captor = ArgumentCaptor.forClass(List.class);
        
        then(articleRepository).should().saveAll(captor.capture());

        List<Article> flatList = captor.getValue();
        Assertions.assertThat(flatList).hasSize(4);
    }
}
