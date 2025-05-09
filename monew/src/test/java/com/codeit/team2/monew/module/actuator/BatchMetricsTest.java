package com.codeit.team2.monew.module.actuator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;

class BatchMetricsTest {

    private MeterRegistry meterRegistry;
    private JobExplorer jobExplorer;
    private BatchMetrics batchMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry(); // 테스트용 메모리 registry
        jobExplorer = mock(JobExplorer.class);
        batchMetrics = new BatchMetrics(meterRegistry, jobExplorer);
    }

    @Test
    void testInitGaugesAndUpdateMetrics() {
        // given
        String jobName = "testJob";
        when(jobExplorer.getJobNames()).thenReturn(Collections.singletonList(jobName));

        JobInstance jobInstance = new JobInstance(1L, jobName);
        JobExecution jobExecution1 = new JobExecution(jobInstance, 1L, null);
        JobExecution jobExecution2 = new JobExecution(jobInstance, 2L, null);

        jobExecution1.setStatus(BatchStatus.COMPLETED);
        jobExecution1.setStartTime(LocalDateTime.now().minusMinutes(10));
        jobExecution1.setEndTime(LocalDateTime.now().minusMinutes(5));

        jobExecution2.setStatus(BatchStatus.FAILED);
        jobExecution2.setStartTime(LocalDateTime.now().minusMinutes(20));
        jobExecution2.setEndTime(LocalDateTime.now().minusMinutes(15));

        StepExecution step1 = new StepExecution("step1", jobExecution1);
        step1.setReadCount(10);
        step1.setWriteCount(8);
        step1.setProcessSkipCount(2);
        jobExecution1.addStepExecutions(Collections.singletonList(step1));

        StepExecution step2 = new StepExecution("step2", jobExecution2);
        step2.setReadCount(5);
        step2.setWriteCount(5);
        step2.setProcessSkipCount(0);
        jobExecution2.addStepExecutions(Collections.singletonList(step2));

        when(jobExplorer.getJobInstances(jobName, 0, Integer.MAX_VALUE))
            .thenReturn(Collections.singletonList(jobInstance));
        when(jobExplorer.getJobExecutions(jobInstance))
            .thenReturn(Arrays.asList(jobExecution1, jobExecution2));

        // when
        batchMetrics.initGauges();
        batchMetrics.updateMetrics();

        // then
        assertThat(meterRegistry.find("batch.job.total.count").tags("job", jobName).gauge().value())
            .isEqualTo(1);
        assertThat(
            meterRegistry.find("batch.job.success.count").tags("job", jobName).gauge().value())
            .isEqualTo(1);
        assertThat(
            meterRegistry.find("batch.job.failure.count").tags("job", jobName).gauge().value())
            .isEqualTo(1);
        assertThat(meterRegistry.find("batch.job.read.count").tags("job", jobName).gauge().value())
            .isEqualTo(15);
        assertThat(meterRegistry.find("batch.job.write.count").tags("job", jobName).gauge().value())
            .isEqualTo(13);
        assertThat(meterRegistry.find("batch.job.skip.count").tags("job", jobName).gauge().value())
            .isEqualTo(2);
    }
}
