package com.codeit.team2.monew.module.domain.article.batch.rss;


import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
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
public class RssBatchConfig {

    private final ItemReader<DummyArticle> dummyArticleReader;
    private final ItemProcessor<DummyArticle, List<ArticleInterestCreateCommand>> articleProcessor;
    private final ItemWriter<List<ArticleInterestCreateCommand>> articleWriter;

    public RssBatchConfig(ItemReader<DummyArticle> dummyArticleReader,
        ItemProcessor<DummyArticle, List<ArticleInterestCreateCommand>> articleProcessor,
        @Qualifier("batchArticleWriter") ItemWriter<List<ArticleInterestCreateCommand>> articleWriter) {
        this.dummyArticleReader = dummyArticleReader;
        this.articleProcessor = articleProcessor;
        this.articleWriter = articleWriter;
    }

    @Bean
    public Step rssArticleBatchStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        DummyArticleCleanupListener listener) {
        return new StepBuilder("rssArticleBatchStep", jobRepository)
            .<DummyArticle, List<ArticleInterestCreateCommand>>chunk(100, transactionManager)
            .reader(dummyArticleReader)
            .processor(articleProcessor)
            .writer(articleWriter)
            .faultTolerant()
            .skip(DataIntegrityViolationException.class)
            .skip(ConstraintViolationException.class)
            .skipLimit(100)
            .listener(listener)
            .build();
    }

    @Bean
    public Job rssArticleBatchJob(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        DummyArticleCleanupListener listener) {
        return new JobBuilder("rssArticleBatchJob", jobRepository)
            .start(rssArticleBatchStep(jobRepository, transactionManager, listener))
            .build();
    }
}
