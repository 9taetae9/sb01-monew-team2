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

    private static final ZoneId TIME_ZONE = ZoneId.of("Asia/Seoul");

    private final LogUploadService logUploadService;

    /**
     * 매일 03:40에 전날 로그 S3에 업로드
     */
    @Scheduled(cron = "0 40 3 * * ?", zone = "#{@timezoneId}")
    public void uploadYesterdayLogs() {
        LocalDate yesterday = LocalDate.now(TIME_ZONE).minusDays(1);
        log.info("Starting scheduled log upload for date: {}", yesterday);

        boolean success = logUploadService.uploadLogByDate(yesterday);

        if (success) {
            log.info("Completed scheduled log upload for date: {}", yesterday);
        } else {
            log.warn("Failed to upload logs for date: {}", yesterday);
        }
    }

    /**
     * 보존 기간보다 오래된 로그 파일 정리
     */
    @Scheduled(cron = "0 40 4 * * ?", zone = "#{@timezoneId}")
    public void cleanupOldLogs() {
        log.info("Starting scheduled log cleanup");
        logUploadService.cleanupOldLogs();
        log.info("Completed scheduled log cleanup");
    }

}
