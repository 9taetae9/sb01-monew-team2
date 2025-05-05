package com.codeit.team2.monew.module.domain.article.backup.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArticleBackupScheduler {

    private final ZoneId zoneId;

    private final JobLauncher jobLauncher;
    private final Job articleBackupJob;

    public ArticleBackupScheduler(
        JobLauncher jobLauncher,
        @Qualifier("articleBackupJob") Job articleBackupJob,
        ZoneId zoneId) {
        this.jobLauncher = jobLauncher;
        this.articleBackupJob = articleBackupJob;
        this.zoneId = zoneId;
    }

    @Scheduled(cron = "0 30 2 * * *", zone = "#{@timezoneId}")
    public void runArticleBackup() {
        try {
            LocalDate yesterday = LocalDate.now(zoneId).minusDays(1);
            String backupDate = yesterday.toString();

            JobParameters params = new JobParametersBuilder()
                .addString("backupDate", backupDate)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

            log.info("Starting article backup job for date: {}", backupDate);
            jobLauncher.run(articleBackupJob, params);

        } catch (Exception e) {
            log.error("Error during article backup: {}", e.getMessage(), e);
        }
    }
}
