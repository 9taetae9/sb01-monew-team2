package com.codeit.team2.monew.module.domain.notification.batch;

import com.codeit.team2.monew.module.domain.notification.repository.NotificationRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class NotificationBatchConfig {

    @Bean
    public Job deleteNotificationJob(JobRepository jobRepository,
        Step deleteNotificationStep) {
        return new JobBuilder("deleteNotificationJob", jobRepository)
            .start(deleteNotificationStep)
            .build();
    }

    @Bean
    public Step deleteNotificationStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet deleteNotificationTasklet) {
        return new StepBuilder("deleteNotificationStep", jobRepository)
            .tasklet(deleteNotificationTasklet, transactionManager)
            .build();
    }

    @Bean
    public Tasklet deleteNotificationTasklet(NotificationRepository notificationRepository) {
        return (contribution, chunkContext) -> {
            Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
            int deletedCount = notificationRepository.deleteByConfirmedIsTrueAndCreatedAtBefore(
                oneWeekAgo);
            log.info("Batch Completed: Confirmed and old notifications deleted: count = {}",
                deletedCount);
            return RepeatStatus.FINISHED;
        };
    }

}


