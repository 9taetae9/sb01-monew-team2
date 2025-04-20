package com.codeit.team2.monew.module.domain.article.batch;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import java.util.List;
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
public class ArticleBatchConfig {

    private final ItemReader<Keyword> keywordReader;
    private final ItemProcessor<Keyword, List<Article>> keywordProcessor;
    private final ItemWriter<List<Article>> articleWriter;

    @Bean
    public Step articleBatchStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager) {
        return new StepBuilder("articleBatchStep", jobRepository)
            .<Keyword, List<Article>>chunk(10, transactionManager)
            .reader(keywordReader)
            .processor(keywordProcessor)
            .writer(articleWriter)
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
