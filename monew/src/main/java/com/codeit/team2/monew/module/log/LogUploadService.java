package com.codeit.team2.monew.module.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogUploadService {

    private static final DateTimeFormatter LOG_FILE_DATE_FORMAT = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd");

    private final S3Client s3Client;
    private final ZoneId zoneId;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.log-prefix:logs}")
    private String s3LogPrefix;

    @Value("${logging.file.path:./logs}")
    private String logFilePath;

    @Value("${logging.file.archive:${logging.file.path}/archive}")
    private String logArchivePath;

    @Value("${logging.retention-days:7}")
    private int logRetentionDays;

    /**
     * 특정 날짜 로그 파일을 압축 후 S3에 업로드
     */
    public boolean uploadLogByDate(LocalDate date) {
        String dateStr = date.format(LOG_FILE_DATE_FORMAT);
        String logFileName = "application." + dateStr + ".log";
        String gzipFileName = logFileName + ".gz";

        Path logFile = Paths.get(logArchivePath, logFileName);
        Path gzipFile = Paths.get(logArchivePath, gzipFileName);

        if (!Files.exists(logFile)) {
            log.warn("Log file for date {} not found at {}", dateStr, logFile);
            return false;
        }

        try {
            compressLogFile(logFile, gzipFile);

            File file = gzipFile.toFile();
            String s3Key = s3LogPrefix + "/" + dateStr + "/" + gzipFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
            log.info("Successfully uploaded log file {} to S3: {}/{}", gzipFileName, bucketName,
                s3Key);

            //업로드 후 압축 파일 삭제
            Files.deleteIfExists(gzipFile);

            return true;
        } catch (Exception e) {
            log.error("Failed to upload log file to S3: {}", e.getMessage(), e);
            return false;
        }


    }

    /**
     * 로그 파일 압축(gzip)
     *
     * @param source 원본 로그 파일
     * @param target 압축 대상 파일
     * @throws IOException
     */
    private void compressLogFile(Path source, Path target) throws IOException {
        try (GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(target.toFile()));
            FileInputStream fis = new FileInputStream(source.toFile())) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzos.write(buffer, 0, len);
            }
        }
    }

    /**
     * 보존 기간 지난 로그 파일 정리
     */
    public void cleanupOldLogs() {
        cleanupOldLogs(this.logRetentionDays);
    }

    /**
     * 보존 기간 지난 로그 파일 정리
     */
    public void cleanupOldLogs(int retentionDays) {
        Path logDir = Paths.get(logArchivePath);
        if (!Files.exists(logDir)) {
            log.warn("Log directory not found: {}", logDir);
            return;
        }

        LocalDate cutoffDate = LocalDate.now(zoneId).minusDays(retentionDays);
        log.info("Cleaning up log files on or before: {} (retention days: {})", cutoffDate,
            retentionDays);

        try (Stream<Path> logFiles = Files.list(logDir)) {
            logFiles
                .filter(Files::isRegularFile)
                .filter(path -> {
                    String fileName = path.getFileName().toString();
                    return fileName.startsWith("application.") && fileName.endsWith(".log");
                })
                .forEach(logFile -> {
                    try {
                        String fileName = logFile.getFileName().toString();
                        String dateStr = fileName.substring(12, 22); // 날짜 추출
                        LocalDate logDate = LocalDate.parse(dateStr);

                        if (!logDate.isAfter(cutoffDate)) {
                            log.info("Deleting old log file: {}", logFile);
                            try {
                                Files.deleteIfExists(logFile);
                                log.debug("Successfully deleted: {}", fileName);
                            } catch (IOException e) {
                                log.error("Failed to delete log file {}: {}", logFile,
                                    e.getMessage(), e);
                            }
                        }
                    } catch (Exception e) {
                        log.error("Failed to process log file {}: {}", logFile, e.getMessage(), e);
                    }
                });
            log.info("Log cleanup completed with retention days: {}", retentionDays);
        } catch (IOException e) {
            log.error("Failed to clean up old logs: {}", e.getMessage(), e);
        }
    }
}
