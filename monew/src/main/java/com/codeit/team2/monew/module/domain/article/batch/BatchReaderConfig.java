package com.codeit.team2.monew.module.domain.article.batch;

import com.codeit.team2.monew.module.domain.article.batch.rss.KeywordCache;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.article.mapper.DummyArticleRowMapper;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchReaderConfig {

    @Bean
    @StepScope
    public JpaPagingItemReader<Keyword> keywordReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<Keyword>()
            .name("keywordReader")
            .entityManagerFactory(emf)
            .queryString("SELECT k FROM Keyword k")
            .pageSize(10)
            .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<DummyArticle> dummyArticleReader(EntityManagerFactory emf) {
        return new JpaPagingItemReaderBuilder<DummyArticle>()
            .name("dummyArticleReader")
            .entityManagerFactory(emf)
            .queryString("SELECT d FROM DummyArticle d")
            .pageSize(100)
            .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<DummyArticle> dummyArticleJdbcReader(
        DataSource dataSource,
        DummyArticleRowMapper rowMapper,
        KeywordCache keywordCache
    ) {
        return new JdbcPagingItemReaderBuilder<DummyArticle>()
            .name("dummyArticleJdbcReader")
            .dataSource(dataSource)
            .selectClause("SELECT *")
            .fromClause("FROM dummy_articles")
            .whereClause("body_tsv @@ to_tsquery('simple', :tsQuery)")
            .parameterValues(Map.of("tsQuery", keywordCache.buildtoTsQuery()))
            .sortKeys(Map.of("id", Order.ASCENDING))
            .rowMapper(rowMapper)
            .pageSize(1000)
            .build();
    }
}
