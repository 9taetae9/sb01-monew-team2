package com.codeit.team2.monew.module.domain.article.batch;

import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class ArticleBatchConfig {

    private final ItemReader<Keyword> keywordReader;
    private final ItemProcessor<Keyword, List<ArticleInterestCreateCommand>> keywordProcessor;
    private final ItemWriter<List<ArticleInterestCreateCommand>> articleWriter;

    public ArticleBatchConfig(ItemReader<Keyword> keywordReader,
        ItemProcessor<Keyword, List<ArticleInterestCreateCommand>> keywordProcessor,
        @Qualifier("batchArticleWriter") ItemWriter<List<ArticleInterestCreateCommand>> articleWriter) {
        this.keywordReader = keywordReader;
        this.keywordProcessor = keywordProcessor;
        this.articleWriter = articleWriter;
    }

    @Bean
    public Step articleBatchStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager) {
        return new StepBuilder("articleBatchStep", jobRepository)
            .<Keyword, List<ArticleInterestCreateCommand>>chunk(10, transactionManager)
            .reader(keywordReader)
            .processor(keywordProcessor)
            .writer(articleWriter)
            .faultTolerant()
            .skip(DataIntegrityViolationException.class)
            .skip(ConstraintViolationException.class)
            .skipLimit(100)
            .build();
    }

    @Bean
    public Job articleBatchJob(JobRepository jobRepository,
        PlatformTransactionManager transactionManager) {
        return new JobBuilder("articleBatchJob", jobRepository)
            .start(articleBatchStep(jobRepository, transactionManager))
            .build();
    }
}
