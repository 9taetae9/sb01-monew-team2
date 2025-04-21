package com.codeit.team2.monew.module.domain.article.batch;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ArticleBatch 를 실행하는 임시 컨트롤러, 실제 운영환경에서 삭제 예정.
 */
@Slf4j
@RestController
@RequestMapping("/api/batch")
public class ArticleBatchTestController {

    private final JobLauncher jobLauncher;

    private final Job articleBatchJob;

    public ArticleBatchTestController(JobLauncher jobLauncher,
        @Qualifier("articleBatchJob") Job articleBatchJob) {
        this.jobLauncher = jobLauncher;
        this.articleBatchJob = articleBatchJob;
    }

    @PostMapping("/run")
    public void runBatch() {
        try {
            JobParameters params = new JobParametersBuilder().addLong("timestamp",
                System.currentTimeMillis()).toJobParameters();
            JobExecution execution = jobLauncher.run(articleBatchJob, params);

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
