package com.codeit.team2.monew.module.log;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LogUploadSchedulerTest {

    @Mock
    private LogUploadService logUploadService;

    private LogUploadScheduler logUploadScheduler;
    private static final ZoneId TIME_ZONE = ZoneId.of("Asia/Seoul");

    @BeforeEach
    void setUp() {
        logUploadScheduler = new LogUploadScheduler(logUploadService, TIME_ZONE);
    }

    @Test
    @DisplayName("전날 로그 업로드 - 성공")
    void uploadYesterdayLogs_Success() {
        LocalDate yesterday = LocalDate.now(TIME_ZONE).minusDays(1);

        when(logUploadService.uploadLogByDate(any(LocalDate.class))).thenReturn(true);

        logUploadScheduler.uploadYesterdayLogs();

        //전달된 날짜가 어제인지 확인
        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(logUploadService).uploadLogByDate(dateCaptor.capture());
        assertEquals(yesterday, dateCaptor.getValue(), "스케줄러는 어제 날짜의 로그를 업로드해야 함");
    }

    @Test
    @DisplayName("전날 로그 업로드 - 실패")
    void uploadYesterdayLogs_Failure() {
        when(logUploadService.uploadLogByDate(any(LocalDate.class))).thenReturn(false);

        logUploadScheduler.uploadYesterdayLogs();

        verify(logUploadService).uploadLogByDate(any(LocalDate.class));
    }

    @Test
    @DisplayName("어제 로그 업로드 - 실패: 예외 발생")
    void uploadYesterdayLogs_Exception() {
        when(logUploadService.uploadLogByDate(any(LocalDate.class)))
            .thenThrow(new RuntimeException("테스트 예외"));

        assertDoesNotThrow(() -> logUploadScheduler.uploadYesterdayLogs(),
            "예외 내부적으로 처리");

        verify(logUploadService).uploadLogByDate(any(LocalDate.class));
    }

    @Test
    @DisplayName("오래된 로그 정리 - 성공")
    void cleanupOldLogs_Success() {
        logUploadScheduler.cleanupOldLogs();

        verify(logUploadService).cleanupOldLogs();
    }

    @Test
    @DisplayName("오래된 로그 정리 - 예외 발생")
    void cleanupOldLogs_Exception() {
        doThrow(new RuntimeException("테스트 예외")).when(logUploadService).cleanupOldLogs();

        assertDoesNotThrow(() -> logUploadScheduler.cleanupOldLogs(),
            "예외를 내부적으로 처리");

        verify(logUploadService).cleanupOldLogs();
    }

    @Test
    @DisplayName("스케줄링 설정 검증 - 올바른 cron 표현식 사용")
    void verifySchedulingConfiguration() throws NoSuchMethodException {
        Method uploadMethod = LogUploadScheduler.class.getMethod("uploadYesterdayLogs");
        Scheduled uploadAnnotation = uploadMethod.getAnnotation(Scheduled.class);

        assertNotNull(uploadAnnotation, "uploadYesterdayLogs 메서드에 @Scheduled 어노테이션이 있어야 함");
        assertEquals("0 30 3 * * ?", uploadAnnotation.cron(),
            "매일 새벽 3시 30분에 실행되도록 설정되어야 함");

        Method cleanupMethod = LogUploadScheduler.class.getMethod("cleanupOldLogs");
        Scheduled cleanupAnnotation = cleanupMethod.getAnnotation(Scheduled.class);

        assertNotNull(cleanupAnnotation, "cleanupOldLogs 메서드에 @Scheduled 어노테이션이 있어야 함");
        assertEquals("0 40 4 * * 0", cleanupAnnotation.cron(),
            "매주 일요일 새벽 4시 40분에 실행되도록 설정되어야 함");
    }
}
