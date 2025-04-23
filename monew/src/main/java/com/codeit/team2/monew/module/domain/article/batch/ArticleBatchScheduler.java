package com.codeit.team2.monew.module.domain.article.batch;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArticleBatchScheduler {

    private final JobLauncher jobLauncher;

    private final Job articleBatchJob;
    private final Job rssArticleBatchJob;

    public ArticleBatchScheduler(JobLauncher jobLauncher,
        @Qualifier("articleBatchJob") Job articleBatchJob,
        @Qualifier("rssArticleBatchJob") Job rssArticleBatchJob) {
        this.jobLauncher = jobLauncher;
        this.articleBatchJob = articleBatchJob;
        this.rssArticleBatchJob = rssArticleBatchJob;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void runArticleBatch() {
        try {
            JobParameters params = new JobParametersBuilder().addLong("timestamp",
                System.currentTimeMillis()).toJobParameters();
            JobExecution execution = jobLauncher.run(articleBatchJob, params);

            if (execution.getStatus() == BatchStatus.COMPLETED) {
                log.info("BATCH SUCCESSFUL, Continuing to rssArticleBatchJob");
                runRssBatch();
            }

        } catch (Exception e) {
            log.info("BATCH JOB FAILED: {}", e.getMessage());
        } finally {
            log.info("BATCH JOB COMPLETED");
        }
    }

    private void runRssBatch() {
        try {
            JobParameters params = new JobParametersBuilder().addLong("timestamp",
                System.currentTimeMillis()).toJobParameters();
            JobExecution execution = jobLauncher.run(rssArticleBatchJob, params);

            if (execution.getStatus() == BatchStatus.COMPLETED) {
                log.info("BATCH SUCCESSFUL");
            }

        } catch (Exception e) {
            log.info("BATCH JOB FAILED: {}", e.getMessage());
        } finally {
            log.info("BATCH JOB COMPLETED");
        }
    }
}
