package com.codeit.team2.monew.module.domain.notification.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.team2.monew.module.domain.notification.service.NotificationService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    @DisplayName("알림 확인 - 성공")
    void confirmNotification() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();

        // when
        mockMvc.perform(
                patch("/api/notifications/{notificationId}", notificationId)
                    .header("Monew-Request-User-Id", userId.toString())
            )
            .andExpect(status().isOk());

        // then
        verify(notificationService).confirmNotification(userId, notificationId);

    }

    @Test
    @DisplayName("알림 전체 확인 - 성공")
    void confirmAllNotifications() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        // when
        mockMvc.perform(
                patch("/api/notifications")
                    .header("Monew-Request-User-Id", userId.toString())
            )
            .andExpect(status().isOk());

        // then
        verify(notificationService).confirmAllNotifications(userId);

    }

    @Test
    @DisplayName("알림 목록 조회 - 성공")
    void findAll() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        // when
        mockMvc.perform(get("/api/notifications")
                .header("Monew-Request-User-Id", userId.toString())
                .param("limit", String.valueOf(50)))
            .andExpect(status().isOk());

        // then
        verify(notificationService).findAll(userId, null, null, 50);

    }


}
