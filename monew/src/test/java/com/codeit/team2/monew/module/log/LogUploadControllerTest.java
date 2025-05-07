package com.codeit.team2.monew.module.log;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.team2.monew.config.WebConfig;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LogUploadController.class)
@AutoConfigureMockMvc(addFilters = false)
@ComponentScan(excludeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = WebConfig.class))
@Import(LogUploadControllerTest.TestConfig.class)
public class LogUploadControllerTest {

    @Configuration
    static class TestConfig {
        @Bean
        public ZoneId zoneId() {
            return ZoneId.of("Asia/Seoul");
        }
    }

    @Autowired
    private Environment environment;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogUploadService logUploadService;

    @Test
    @DisplayName("로그 업로드 - 날짜 미지정 시 어제 날짜 사용")
    void testUploadLogs_WithoutDate() throws Exception {
        when(logUploadService.uploadLogByDate(any(LocalDate.class))).thenReturn(true);

        mockMvc.perform(post("/admin/logs/upload"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Successfully uploaded logs for date:")));

        verify(logUploadService).uploadLogByDate(any(LocalDate.class));
    }

    @Test
    @DisplayName("로그 업로드 - 특정 날짜 지정")
    void testUploadLogs_WithDate() throws Exception {
        LocalDate specificDate = LocalDate.parse("2025-05-05");
        when(logUploadService.uploadLogByDate(eq(specificDate))).thenReturn(true);

        mockMvc.perform(post("/admin/logs/upload")
                .param("date", "2025-05-05")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Successfully uploaded logs for date: 2025-05-05")));

        verify(logUploadService).uploadLogByDate(eq(specificDate));
    }

    @Test
    @DisplayName("로그 업로드 - 실패 케이스")
    void testUploadLogs_Failure() throws Exception {
        LocalDate specificDate = LocalDate.parse("2025-05-05");
        when(logUploadService.uploadLogByDate(eq(specificDate))).thenReturn(false);

        mockMvc.perform(post("/admin/logs/upload")
                .param("date", "2025-05-05")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(containsString("Failed to upload logs for date: 2025-05-05")));
    }

    @Test
    @DisplayName("로그 정리 - 기본 보존 기간 사용")
    void testCleanupLogs_DefaultRetention() throws Exception {
        String defaultRetentionDays = environment.getProperty("logging.retention-days");
        String expectedResponse = "Log cleanup initiated with default retention days(" + defaultRetentionDays + ")";

        mockMvc.perform(post("/admin/logs/cleanup"))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));

        verify(logUploadService).cleanupOldLogs();
        verify(logUploadService, never()).cleanupOldLogs(anyInt());
    }

    @Test
    @DisplayName("로그 정리 - 사용자 지정 보존 기간 사용")
    void testCleanupLogs_CustomRetention() throws Exception {
        mockMvc.perform(post("/admin/logs/cleanup")
                .param("retentionDays", "14")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
            .andExpect(status().isOk())
            .andExpect(content().string("Log cleanup initiated with retention days: 14"));

        verify(logUploadService, never()).cleanupOldLogs();
        verify(logUploadService).cleanupOldLogs(14);
    }
}
