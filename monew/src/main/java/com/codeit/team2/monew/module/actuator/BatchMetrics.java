package com.codeit.team2.monew.module.actuator;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!test & !test-temp & !test-postgre")
@RequiredArgsConstructor
public class BatchMetrics {

    private final MeterRegistry meterRegistry;
    private final JobExplorer jobExplorer;

    private final Map<String, AtomicLong> totalCountMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> successCountMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> failureCountMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> lastDurationMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> avgDurationMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> readCountMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> writeCountMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> skipCountMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void initGauges() {
        if (jobExplorer.getJobNames().isEmpty()) {
            return;
        }
        for (String jobName : jobExplorer.getJobNames()) {
            Tags tags = Tags.of("job", jobName);
            totalCountMap.put(jobName, registerGauge("batch.job.total.count", tags));   // 총 실행 횟수
            successCountMap.put(jobName,
                registerGauge("batch.job.success.count", tags));   // 성공한(COMPLETED) 횟수
            failureCountMap.put(jobName,
                registerGauge("batch.job.failure.count", tags));   // 실패(FAILED) 횟수
            lastDurationMap.put(jobName,
                registerGauge("batch.job.last.duration.millis", tags));    // 마지막 Job의 실행 시간
            avgDurationMap.put(jobName,
                registerGauge("batch.job.avg.duration.millis", tags));  // 전체 Job들의 평균 소요 시간
            readCountMap.put(jobName,
                registerGauge("batch.job.read.count", tags));     // 모든 Job에서 처리한 read 횟수 합
            writeCountMap.put(jobName,
                registerGauge("batch.job.write.count", tags));   // 모든 Job에서 처리한 write 횟수 합
            skipCountMap.put(jobName,
                registerGauge("batch.job.skip.count", tags)); // 모든 Job에서 처리한 skip 횟수 합
        }
    }

    private AtomicLong registerGauge(String name, Tags tags) {
        AtomicLong gauge = new AtomicLong(0);
        meterRegistry.gauge(name, tags, gauge);
        return gauge;
    }

    @Scheduled(fixedDelay = 10000)  // 10초마다 Map 업데이트
    public void updateMetrics() {
        for (String jobName : jobExplorer.getJobNames()) {
            List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0,
                Integer.MAX_VALUE);

            long totalCount = jobInstances.size();
            long successCount = 0;
            long failureCount = 0;
            long totalDurationMillis = 0;
            long lastDurationMillis = 0;

            long totalReadCount = 0;
            long totalWriteCount = 0;
            long totalSkipCount = 0;

            JobExecution lastExecution = null;

            for (JobInstance instance : jobInstances) {
                for (JobExecution execution : jobExplorer.getJobExecutions(instance)) {
                    if (execution.getStatus() == BatchStatus.COMPLETED) {
                        successCount++;
                    }
                    if (execution.getStatus() == BatchStatus.FAILED) {
                        failureCount++;
                    }

                    if (execution.getStartTime() != null && execution.getEndTime() != null) {
                        long duration = Duration.between(
                            execution.getStartTime(), execution.getEndTime()).toMillis();
                        totalDurationMillis += duration;
                        if (lastExecution == null || execution.getEndTime()
                            .isAfter(lastExecution.getEndTime())) {
                            lastExecution = execution;
                            lastDurationMillis = duration;
                        }
                    }

                    for (StepExecution stepExecution : execution.getStepExecutions()) {
                        totalReadCount += stepExecution.getReadCount();
                        totalWriteCount += stepExecution.getWriteCount();
                        totalSkipCount += stepExecution.getSkipCount();
                    }
                }
            }

            double avgDurationMillis =
                totalCount > 0 ? (double) totalDurationMillis / totalCount : 0;

            totalCountMap.get(jobName).set(totalCount);
            successCountMap.get(jobName).set(successCount);
            failureCountMap.get(jobName).set(failureCount);
            lastDurationMap.get(jobName).set(lastDurationMillis);
            avgDurationMap.get(jobName).set((long) avgDurationMillis);
            readCountMap.get(jobName).set(totalReadCount);
            writeCountMap.get(jobName).set(totalWriteCount);
            skipCountMap.get(jobName).set(totalSkipCount);
        }
    }
}
