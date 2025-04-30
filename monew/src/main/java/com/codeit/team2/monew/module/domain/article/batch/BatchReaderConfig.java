package com.codeit.team2.monew.module.domain.article.batch;

import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
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
}
