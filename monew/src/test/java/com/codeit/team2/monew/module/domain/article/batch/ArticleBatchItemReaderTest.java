package com.codeit.team2.monew.module.domain.article.batch;

// TODO : Keyword repository 완료시 작업

import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@JdbcTest
@Import({KeywordReaderConfig.class})
public class ArticleBatchItemReaderTest {


    @Autowired
    private JpaPagingItemReader<Keyword> keywordReader;

    @Test
    @Sql("/insert-keywords.sql")
    void keywordReader_shouldReturn_keywords() throws Exception {
        keywordReader.open(new ExecutionContext());

        Keyword keyword = keywordReader.read();

        Assertions.assertThat(keyword).isNotNull();
        System.out.println(keyword);
        keywordReader.close();
    }
}
