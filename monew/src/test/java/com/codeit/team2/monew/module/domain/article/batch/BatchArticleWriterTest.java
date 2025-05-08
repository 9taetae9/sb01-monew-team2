package com.codeit.team2.monew.module.domain.article.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.notification.service.NotificationService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
public class BatchArticleWriterTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private NotificationService notificationService;
    private BatchArticleWriter writer;

    @BeforeEach
    void setup() {
        writer = new BatchArticleWriter(articleRepository, jdbcTemplate, notificationService);
    }


    @Test
    void testWriteToRepository_success() throws Exception {
        //given

        Article a = new Article("a", "a", "a", "a", Collections.emptySet(), 0L, Instant.now(),
            false, null);
        Article b = new Article("b", "b", "b", "b", Collections.emptySet(), 0L, Instant.now(),
            false, null);
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

        //when
        writer.write(combined);

        // then
        then(jdbcTemplate).should(times(2)).batchUpdate(any(), any(), anyInt(), any());

    }
}
