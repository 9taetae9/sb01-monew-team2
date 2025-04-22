package com.codeit.team2.monew.module.domain.article.batch;


import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.HankyungNewsClient;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final HankyungNewsClient hankyungNewsClient;

    public ArticleBatchTestController(JobLauncher jobLauncher,
        @Qualifier("articleBatchJob") Job articleBatchJob,
        HankyungNewsClient hankyungNewsClient) {
        this.jobLauncher = jobLauncher;
        this.articleBatchJob = articleBatchJob;
        this.hankyungNewsClient = hankyungNewsClient;
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


    @GetMapping("/han")
    public ResponseEntity<List<Article>> getHankyungArticles() {
        List<Article> articles = hankyungNewsClient.fetchArticles(
            new FetchCommand("tmp", 0, 0, "none"));
        return ResponseEntity.ok(articles);
    }
}
