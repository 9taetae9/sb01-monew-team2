package com.codeit.team2.monew.module.domain.notification.batch;

import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationScheduler {

    private final ZoneId zoneId;

    private final JobLauncher jobLauncher;
    private final Job deleteNotificationJob;

    public NotificationScheduler(
        JobLauncher jobLauncher,
        @Qualifier("deleteNotificationJob") Job deleteNotificationJob, ZoneId zoneId
    ) {
        this.jobLauncher = jobLauncher;
        this.deleteNotificationJob = deleteNotificationJob;
        this.zoneId = zoneId;
    }

    @Scheduled(cron = "0 30 4 * * *", zone = "#{@timezoneId}")
    public void runJob() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();
        log.info("Batch Processing Start: Delete confirmed and old notifications");
        jobLauncher.run(deleteNotificationJob, parameters);
    }
}
