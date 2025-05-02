package com.codeit.team2.monew.module.domain.article.backup.batch;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleBackupReaderConfig {

    @Bean
    @StepScope
    public JpaPagingItemReader<Article> articleBackupReader(
        EntityManagerFactory emf,
        @Value("#{jobParameters['backupDate']}") String backupDateStr) {

        // 백업 날짜 파라미터 처리
        LocalDate backupDate = LocalDate.parse(backupDateStr);
        Instant startOfDay = backupDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = backupDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        return new JpaPagingItemReaderBuilder<Article>()
            .name("articleBackupReader")
            .entityManagerFactory(emf)
            .queryString("SELECT a FROM Article a WHERE " +
                "a.createdAt >= :startDate AND a.createdAt < :endDate")
            .parameterValues(Map.of(
                "startDate", startOfDay,
                "endDate", endOfDay
            ))
            .pageSize(50)
            .build();
    }
}
