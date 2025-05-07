package com.codeit.team2.monew.module.domain.useractivity.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.service.UserActivityService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserActivityController.class)
class UserActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserActivityService userActivityService;

    @Test
    void 사용자_활동_내역_조회_성공() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID loginId = userId;
        String email = "email@a.com";
        String nickname = "nickname";
        Instant createdAt = Instant.now();

        UserActivityDto dto = new UserActivityDto(
            userId,
            email,
            nickname,
            createdAt,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>()
        );

        // when
        when(userActivityService.findUserActivities(loginId, userId)).thenReturn(dto);

        // then
        mockMvc.perform(
                get("/api/user-activities/{userId}", userId)
                    .header("Monew-Request-User-Id", loginId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.nickname").value(nickname))
            .andExpect(jsonPath("$.createdAt").value(createdAt.toString()))
            .andExpect(jsonPath("$.subscriptions", hasSize(0)))
            .andExpect(jsonPath("$.comments", hasSize(0)))
            .andExpect(jsonPath("$.commentLikes", hasSize(0)))
            .andExpect(jsonPath("$.articleViews", hasSize(0)));
    }

}
