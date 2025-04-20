package com.codeit.team2.monew.module.domain.article.batch;

// TODO : Keyword repository 완료시 작업

import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

@JdbcTest
@Import({KeywordReaderConfig.class})
public class ArticleBatchItemReaderTest {


    @Autowired
    private JdbcPagingItemReader<Keyword> keywordReader;

    @Test
    void keywordReader_shouldReturn_keywords() throws Exception {
        keywordReader.open(new ExecutionContext());

        Keyword keyword = keywordReader.read();

        Assertions.assertThat(keyword).isNotNull();
    }
}
