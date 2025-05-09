package com.codeit.team2.monew.module.log;

import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogUploadScheduler {

    private final LogUploadService logUploadService;
    private final ZoneId zoneId;

    /**
     * 매일 03:30에 전날 로그 S3에 업로드
     */
    @Scheduled(cron = "0 30 3 * * ?", zone = "#{@timezoneId}")
    public void uploadYesterdayLogs() {
        LocalDate yesterday = LocalDate.now(zoneId).minusDays(1);
        log.info("Starting scheduled log upload for date: {}", yesterday);

        try {
            boolean success = logUploadService.uploadLogByDate(yesterday);
            if (success) {
                log.info("Completed scheduled log upload for date: {}", yesterday);
            } else {
                log.warn("Failed to upload logs for date: {}", yesterday);
            }
        } catch (Exception e) {
            log.error("Error occurred during log upload for date {}: {}", yesterday, e.getMessage(), e);
        }
    }

    /**
     * 보존 기간보다 오래된 로그 파일 정리
     */
    @Scheduled(cron = "0 40 4 * * 0", zone = "#{@timezoneId}")
    public void cleanupOldLogs() {
        log.info("Starting scheduled log cleanup");
        try {
            logUploadService.cleanupOldLogs();
            log.info("Completed scheduled log cleanup");
        } catch (Exception e) {
            log.error("Error occurred during log cleanup: {}", e.getMessage(), e);
        }
    }

}
