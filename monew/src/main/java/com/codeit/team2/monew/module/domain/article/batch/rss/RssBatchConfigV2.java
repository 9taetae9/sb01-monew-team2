package com.codeit.team2.monew.module.domain.article.batch.rss;


import com.codeit.team2.monew.module.domain.article.dto.ArticleInterestCreateCommand;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
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
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class RssBatchConfigV2 {

    private final ItemReader<DummyArticle> dummyArticleJdbcReader;
    private final ItemProcessor<DummyArticle, List<ArticleInterestCreateCommand>> dummyArticleProcessorV2;
    private final ItemWriter<List<ArticleInterestCreateCommand>> articleWriter;


    public RssBatchConfigV2(
        @Qualifier("dummyArticleJdbcReader") ItemReader<DummyArticle> dummyArticleJdbcReader,
        @Qualifier("dummyArticleProcessorV2") ItemProcessor<DummyArticle, List<ArticleInterestCreateCommand>> dummyArticleProcessorV2,
        @Qualifier("batchArticleWriter") ItemWriter<List<ArticleInterestCreateCommand>> articleWriter) {
        this.dummyArticleJdbcReader = dummyArticleJdbcReader;
        this.dummyArticleProcessorV2 = dummyArticleProcessorV2;
        this.articleWriter = articleWriter;
    }

    @Bean
    public Step rssArticleBatchStepV2(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        DummyArticleCleanupListener listener) {
        return new StepBuilder("rssArticleBatchStepV2", jobRepository)
            .<DummyArticle, List<ArticleInterestCreateCommand>>chunk(1000, transactionManager)
            .reader(dummyArticleJdbcReader)
            .processor(dummyArticleProcessorV2)
            .writer(articleWriter)
            .listener(listener)
            .build();
    }

    @Bean
    public Job rssArticleBatchJobV2(JobRepository jobRepository,
        Step rssArticleBatchStepV2,
        DummyArticleCleanupListener listener) {
        return new JobBuilder("rssArticleBatchJobV2", jobRepository)
            .start(rssArticleBatchStepV2)
            .build();
    }


}
