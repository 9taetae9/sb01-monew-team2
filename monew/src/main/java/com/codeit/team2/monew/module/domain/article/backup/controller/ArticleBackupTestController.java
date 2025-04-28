package com.codeit.team2.monew.module.domain.article.backup.controller;

import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ArticleBackup을 수동으로 실행하는 테스트 컨트롤러, 배포 환경에서 삭제 고려
 */
@Slf4j
@RestController
@RequestMapping("/api/backup")
public class ArticleBackupTestController {

    private final JobLauncher jobLauncher;
    private final Job articleBackupJob;

    public ArticleBackupTestController(
        JobLauncher jobLauncher,
        @Qualifier("articleBackupJob") Job articleBackupJob) {
        this.jobLauncher = jobLauncher;
        this.articleBackupJob = articleBackupJob;
    }

    @PostMapping("/run")
    public ResponseEntity<String> runBackup(
        @RequestParam(value = "date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate backupDate) {

        try {
            if (backupDate == null) {
                backupDate = LocalDate.now().minusDays(1); // (dev)날짜가 제공되지 않으면 어제 날짜 사용
//                backupDate = LocalDate.now();
            }

            String backupDateStr = backupDate.toString();

            JobParameters params = new JobParametersBuilder()
                .addString("backupDate", backupDateStr)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

            log.info("Starting manual article backup job for date: {}", backupDateStr);
            jobLauncher.run(articleBackupJob, params);

            return ResponseEntity.ok("Backup job started for date: " + backupDateStr);

        } catch (Exception e) {
            log.error("Error during article backup: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Backup failed: " + e.getMessage());
        }
    }
}
