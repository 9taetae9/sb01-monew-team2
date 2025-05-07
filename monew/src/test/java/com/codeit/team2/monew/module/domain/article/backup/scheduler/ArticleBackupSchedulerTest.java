package com.codeit.team2.monew.module.domain.article.backup.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

@ExtendWith(MockitoExtension.class)
public class ArticleBackupSchedulerTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job articleBackupJob;

    @Mock
    private JobExecution jobExecution;

    private ArticleBackupScheduler scheduler;
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    @BeforeEach
    void setUp() {
        scheduler = new ArticleBackupScheduler(jobLauncher, articleBackupJob, SEOUL_ZONE);
    }

    @Test
    @DisplayName("기사 백업 스케줄링 - 성공")
    void testRunArticleBackup_Success() throws Exception {
        when(jobLauncher.run(eq(articleBackupJob), any(JobParameters.class))).thenReturn(jobExecution);

        scheduler.runArticleBackup();

        verify(jobLauncher).run(eq(articleBackupJob), any(JobParameters.class));
    }

    @Test
    @DisplayName("기사 백업 스케줄링 - 실패: 예외 발생")
    void testRunArticleBackup_Exception() throws Exception {
        when(jobLauncher.run(eq(articleBackupJob), any(JobParameters.class)))
            .thenThrow(new RuntimeException("Job failed"));

        scheduler.runArticleBackup();

        verify(jobLauncher).run(eq(articleBackupJob), any(JobParameters.class));
    }
}
