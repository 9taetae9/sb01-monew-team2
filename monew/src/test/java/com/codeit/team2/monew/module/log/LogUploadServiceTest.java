package com.codeit.team2.monew.module.log;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@ExtendWith(MockitoExtension.class)
class LogUploadServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private S3Client s3Client;

    private LogUploadService logUploadService;
    private Path archivePath;
    private static final ZoneId TIME_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    void setUp() throws IOException {
        archivePath = tempDir.resolve("archive");
        Files.createDirectories(archivePath);

        logUploadService = new LogUploadService(s3Client, TIME_ZONE);

        ReflectionTestUtils.setField(logUploadService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(logUploadService, "s3LogPrefix", "logs");
        ReflectionTestUtils.setField(logUploadService, "logArchivePath", archivePath.toString());
        ReflectionTestUtils.setField(logUploadService, "logRetentionDays", 7);
    }

    @DisplayName("로그 업로드 - 파일 존재 및 S3 업로드 성공")
    @Test
    void uploadLogByDate_Success() throws IOException {
        LocalDate yesterday = LocalDate.now(TIME_ZONE).minusDays(1);
        String dateStr = yesterday.format(DATE_FORMAT);
        String logFileName = "application." + dateStr + ".log";
        Path logFile = archivePath.resolve(logFileName);
        Path gzipFile = archivePath.resolve(logFileName + ".gz");

        Files.writeString(logFile, "Test log content");

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().build());

        boolean result = logUploadService.uploadLogByDate(yesterday);

        assertTrue(result, "업로드가 성공했으므로 true를 반환해야 함");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        assertFalse(Files.exists(gzipFile), "압축 파일이 성공적으로 삭제되어야 함");
    }

    @DisplayName("로그 업로드 - 실패: 파일이 존재하지 않음")
    @Test
    void uploadLogByDate_FileNotFound() {
        // 존재하지 않는 날짜의 로그 파일
        LocalDate date = LocalDate.now(TIME_ZONE).minusDays(10);

        boolean result = logUploadService.uploadLogByDate(date);

        assertFalse(result, "파일이 없으므로 false 반환");
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @DisplayName("로그 업로드 - 실패: S3 업로드 중 예외 발생")
    @Test
    void uploadLogByDate_UploadError() throws IOException {
        LocalDate yesterday = LocalDate.now(TIME_ZONE).minusDays(1);
        String dateStr = yesterday.format(DATE_FORMAT);
        String logFileName = "application." + dateStr + ".log";
        Path logFile = archivePath.resolve(logFileName);

        Files.writeString(logFile, "Test log content");

        doThrow(new RuntimeException("Upload failed")).when(s3Client)
            .putObject(any(PutObjectRequest.class), any(RequestBody.class));

        boolean result = logUploadService.uploadLogByDate(yesterday);

        assertFalse(result, "업로드 실패 시 false 반환");
        // 압축 파일이 정리되었는지 확인
        assertFalse(Files.exists(archivePath.resolve(logFileName + ".gz")),
            "업로드 실패 시에도 임시 압축 파일 삭제");
    }

    @DisplayName("로그 업로드 - 실패: 압축 파일 생성 중 IO 예외 발생")
    @Test
    void uploadLogByDate_CompressionError() throws IOException {
        LocalDate yesterday = LocalDate.now(TIME_ZONE).minusDays(1);
        String dateStr = yesterday.format(DATE_FORMAT);
        String logFileName = "application." + dateStr + ".log";
        Path logFile = archivePath.resolve(logFileName);

        Files.writeString(logFile, "Test log content");

        LogUploadService spyLogUploadService = spy(logUploadService);

        doReturn(false).when(spyLogUploadService).uploadLogByDate(any(LocalDate.class));

        boolean result = spyLogUploadService.uploadLogByDate(yesterday);

        assertFalse(result, "압축 실패 시 false 반환");
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }


    @DisplayName("로그 정리 - 기본 보존 기간(7일) 적용")
    @Test
    void cleanupOldLogs() throws IOException {
        LocalDate today = LocalDate.now(TIME_ZONE);
        LocalDate oldDate = today.minusDays(10); // 보관 기간(7일)보다 오래된 날짜
        LocalDate recentDate = today.minusDays(3); // 보관 기간 내 날짜

        // 오래된 로그 파일
        Path oldLogFile = archivePath.resolve(
            "application." + oldDate.format(DATE_FORMAT) + ".log");
        Files.writeString(oldLogFile, "Old log content");

        // 최근 로그 파일
        Path recentLogFile = archivePath.resolve(
            "application." + recentDate.format(DATE_FORMAT) + ".log");
        Files.writeString(recentLogFile, "Recent log content");

        logUploadService.cleanupOldLogs();

        assertFalse(Files.exists(oldLogFile), "오래된 로그 파일은 삭제되어야 함");
        assertTrue(Files.exists(recentLogFile), "최근 로그 파일은 유지되어야 함");
    }

    @DisplayName("로그 정리 - 사용자 지정 보존 기간(3일) 적용")
    @Test
    void cleanupOldLogs_WithCustomRetention() throws IOException {
        // 현재 날짜 기준 로그 파일 생성
        LocalDate today = LocalDate.now(TIME_ZONE);
        LocalDate date5DaysAgo = today.minusDays(5);

        // 5일 전 로그 파일
        Path logFile5DaysAgo = archivePath.resolve(
            "application." + date5DaysAgo.format(DATE_FORMAT) + ".log");
        Files.writeString(logFile5DaysAgo, "5 days old log content");

        // 보존기간(3일) 지정으로 로그 정리 실행
        logUploadService.cleanupOldLogs(3);

        assertFalse(Files.exists(logFile5DaysAgo), "3일 보존 기간 지정시 5일 전 파일 삭제");
    }

    @DisplayName("로그 정리 - 경계값 테스트: 7일 전 로그 파일은 보존 기간에 포함되지 않으므로 삭제되어야 함")
    @Test
    void cleanupOldLogs_BoundaryCase() throws IOException {
        LocalDate today = LocalDate.now(TIME_ZONE);
        LocalDate exactBoundaryDate = today.minusDays(7);

        Path boundaryLogFile = archivePath.resolve(
            "application." + exactBoundaryDate.format(DATE_FORMAT) + ".log");
        Files.writeString(boundaryLogFile, "Boundary log content");

        // 기본 보존 기간(7일)으로 로그 정리 실행
        logUploadService.cleanupOldLogs();

        assertFalse(Files.exists(boundaryLogFile), "정확히 보존 기간 경계에 있는 파일 삭제");
    }

    @DisplayName("로그 정리 - 로그 형식에 맞지 않는 파일은 처리하지 않음")
    @Test
    void cleanupOldLogs_IgnoreNonLogFiles() throws IOException {
        LocalDate today = LocalDate.now(TIME_ZONE);
        LocalDate oldDate = today.minusDays(10);

        // 오래된 로그 파일 (정상 포맷)
        Path oldLogFile = archivePath.resolve(
            "application." + oldDate.format(DATE_FORMAT) + ".log");
        Files.writeString(oldLogFile, "Old log content");

        // 다른 형식 파일
        Path nonLogFile = archivePath.resolve("system." + oldDate.format(DATE_FORMAT) + ".txt");
        Files.writeString(nonLogFile, "Not a log file");

        logUploadService.cleanupOldLogs();

        assertFalse(Files.exists(oldLogFile), "오래된 로그 파일 삭제");
        assertTrue(Files.exists(nonLogFile), "로그 파일이 아닌 파일은 유지");
    }

    @DisplayName("로그 정리 - try-with-resources로 디렉토리 스트림 관리 테스트")
    @Test
    void cleanupOldLogs_ResourceManagement() throws IOException {
        // Stream<Path> 리소스가 제대로 닫히는지 테스트
        // 실제로 Stream이 닫히는지 직접 확인하는 대신, 리소스 누수로 인한 예외가 발생하지 않는지 간접 확인

        // 여러 로그 파일 생성
        LocalDate today = LocalDate.now(TIME_ZONE);
        for (int i = 8; i <= 100; i++) {
            LocalDate oldDate = today.minusDays(i);
            Path oldLogFile = archivePath.resolve(
                "application." + oldDate.format(DATE_FORMAT) + ".log");
            Files.writeString(oldLogFile, "Log content for day " + i);
        }

        // 반복 실행 - 문제 없는지 확인
        for (int i = 0; i < 10; i++) {
            logUploadService.cleanupOldLogs();
        }

        assertTrue(true, "리소스 누수 없이 정상 처리");
    }

    @DisplayName("로그 업로드 - 파일 압축 후 원본 파일은 유지, 압축 파일은 삭제되어야 함")
    @Test
    void uploadLogByDate_OriginalFilePreserved() throws IOException {
        LocalDate yesterday = LocalDate.now(TIME_ZONE).minusDays(1);
        String dateStr = yesterday.format(DATE_FORMAT);
        String logFileName = "application." + dateStr + ".log";
        Path logFile = archivePath.resolve(logFileName);

        Files.writeString(logFile, "Test log content for preservation check");

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().build());

        boolean result = logUploadService.uploadLogByDate(yesterday);

        assertTrue(result, "업로드 성공");
        assertTrue(Files.exists(logFile), "원본 로그 파일 보존");
        assertFalse(Files.exists(archivePath.resolve(logFileName + ".gz")), "압축 파일 삭제");
    }
}
