package com.codeit.team2.monew.module.domain.article.backup.batch;

import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleBackupDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class ArticleBackupConfig {

    private final ItemReader<Article> articleBackupReader;
    private final ItemProcessor<Article, ArticleBackupDto> articleBackupProcessor;
    private final ItemWriter<ArticleBackupDto> articleBackupWriter;

    @Bean
    public Step articleBackupStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager) {
        return new StepBuilder("articleBackupStep", jobRepository)
            .<Article, ArticleBackupDto>chunk(100, transactionManager)
            .reader(articleBackupReader)
            .processor(articleBackupProcessor)
            .writer(articleBackupWriter)
            .build();
    }

    @Bean
    public Job articleBackupJob(JobRepository jobRepository,
        PlatformTransactionManager transactionManager) {
        return new JobBuilder("articleBackupJob", jobRepository)
            .start(articleBackupStep(jobRepository, transactionManager))
            .build();
    }
}
