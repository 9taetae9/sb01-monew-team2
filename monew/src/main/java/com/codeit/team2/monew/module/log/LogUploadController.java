package com.codeit.team2.monew.module.log;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
public class LogUploadController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final LogUploadService logUploadService;
    private final ZoneId zoneId;
    private final Environment environment;

    /**
     * 특정 날짜(default: 전날)의 로그 수동으로 S3에 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadLogs(
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE)LocalDate date) {

        if (date == null) {
            date = LocalDate.now(zoneId).minusDays(1);
        }

        boolean success = logUploadService.uploadLogByDate(date);

        if(success) {
            return ResponseEntity.ok("Successfully uploaded logs for date: " + date.format(DATE_FORMAT));
        } else {
            return ResponseEntity.badRequest().body("Failed to upload logs for date: " + date.format(DATE_FORMAT));
        }
    }

    /**
     * 오래된 로그 파일 수동으로 정리
     * retentionDays 지정 시 해당 일수이전 로그 파일 삭제
     * @return
     */
    @PostMapping("/cleanup")
    public ResponseEntity<String> cleanupLogs(
        @RequestParam(required = false) Integer retentionDays) {
        if (retentionDays != null) {
            logUploadService.cleanupOldLogs(retentionDays);
            return ResponseEntity.ok("Log cleanup initiated with retention days: " + retentionDays);
        } else {
            logUploadService.cleanupOldLogs();
            String defaultDays = environment.getProperty("logging.retention-days");
            return ResponseEntity.ok("Log cleanup initiated with default retention days(" + defaultDays+")");
        }
    }
}
