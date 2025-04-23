package com.codeit.team2.monew.module.domain.article.batch;


import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.article.external.ChosunRssNewsClient;
import com.codeit.team2.monew.module.domain.article.external.HankyungRssNewsClient;
import com.codeit.team2.monew.module.domain.article.external.YonhapRssNewsClient;
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
    private final HankyungRssNewsClient hankyungNewsClient;
    private final ChosunRssNewsClient chosunRssNewsClient;
    private final YonhapRssNewsClient yonhapRssNewsClient;

    public ArticleBatchTestController(JobLauncher jobLauncher,
        @Qualifier("articleBatchJob") Job articleBatchJob,
        HankyungRssNewsClient hankyungNewsClient,
        ChosunRssNewsClient chosunRssNewsClient,
        YonhapRssNewsClient yonhapRssNewsClient) {
        this.jobLauncher = jobLauncher;
        this.articleBatchJob = articleBatchJob;
        this.hankyungNewsClient = hankyungNewsClient;
        this.chosunRssNewsClient = chosunRssNewsClient;
        this.yonhapRssNewsClient = yonhapRssNewsClient;
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
    public ResponseEntity<List<DummyArticle>> getHankyungArticles() {
        List<DummyArticle> articles = hankyungNewsClient.fetchArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/chosun")
    public ResponseEntity<List<DummyArticle>> getChosunArticles() {
        List<DummyArticle> articles = chosunRssNewsClient.fetchArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/yonhap")
    public ResponseEntity<List<DummyArticle>> getYonhapArticles() {
        List<DummyArticle> articles = yonhapRssNewsClient.fetchArticles();
        return ResponseEntity.ok(articles);
    }
}
