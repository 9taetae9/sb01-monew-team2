package com.codeit.team2.monew.module.domain.interest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.team2.monew.module.domain.interest.dto.request.CursorPageRequestInterestDto;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.CursorPageResponseInterestDto;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.exception.InterestErrorCode;
import com.codeit.team2.monew.module.domain.interest.exception.InterestNotFoundException;
import com.codeit.team2.monew.module.domain.interest.service.InterestService;
import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
import com.codeit.team2.monew.module.domain.subscription.service.SubscriptionService;
import com.codeit.team2.monew.module.domain.user.TestUserFactory;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(InterestController.class)
class InterestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterestService interestService;

    @MockitoBean
    private SubscriptionService subscriptionService;

    @DisplayName("관심사 생성에 성공합니다.")
    @Test
    void create() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        String name = "채소";
        List<String> keywords = List.of("당근");

        InterestRegisterRequest requestDto = new InterestRegisterRequest(
            name,
            keywords
        );

        InterestDto interestDto = new InterestDto(
            UUID.randomUUID(),
            name,
            keywords,
            0,
            false
        );

        when(interestService.create(requestDto, userId))
            .thenReturn(interestDto);

        //when & then
        mockMvc.perform(post("/api/interests")
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
            ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.keywords[0]").value(keywords.get(0)))
            .andExpect(jsonPath("$.subscriberCount").value(interestDto.subscriberCount()))
            .andExpect(jsonPath("$.subscribedByMe").value(interestDto.subscribedByMe()));

    }

    @DisplayName("관심사명이 없어 예외를 응답합니다.")
    @Test
    void create_failure() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        String name = "";
        List<String> keywords = List.of("당근");

        InterestRegisterRequest requestDto = new InterestRegisterRequest(
            name,
            keywords
        );

        InterestDto interestDto = new InterestDto(
            UUID.randomUUID(),
            name,
            keywords,
            0,
            false
        );

        when(interestService.create(requestDto, userId))
            .thenReturn(interestDto);

        //when & then
        mockMvc.perform(post("/api/interests")
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
            ).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"))
            .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
            .andExpect(jsonPath("$.message").value("관심사명을 비워둘 수 없습니다.")) // CommonErrorCode 메시지
            .andExpect(jsonPath("$.details").exists())
            .andExpect(jsonPath("$.details.name").value(""));

    }

    @DisplayName("관심사 키워드 수정에 성공합니다.")
    @Test
    void update() throws Exception {
        //given
        UUID userId = UUID.randomUUID();
        UUID interestId = UUID.randomUUID();
        String name = "채소";
        List<String> keywords = List.of("시금치");

        InterestUpdateRequest requestDto = new InterestUpdateRequest(
            keywords
        );

        InterestDto interestDto = new InterestDto(
            interestId,
            name,
            keywords,
            1,
            true
        );

        when(interestService.update(requestDto, interestId, userId))
            .thenReturn(interestDto);

        //when & then
        mockMvc.perform(patch("/api/interests/{interestId}", interestId)
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.keywords[0]").value(keywords.get(0)))
            .andExpect(jsonPath("$.subscriberCount").value(interestDto.subscriberCount()))
            .andExpect(jsonPath("$.subscribedByMe").value(interestDto.subscribedByMe()));
    }

    @DisplayName("관심사를 찾을 수 없어 예외를 응답합니다.")
    @Test
    void update_failure() throws Exception {
        //given
        UUID interestId = UUID.randomUUID();
        List<String> keywords = List.of("시금치");
        User user = TestUserFactory.createWithName("이름");

        InterestUpdateRequest requestDto = new InterestUpdateRequest(
            keywords
        );

        when(interestService.update(requestDto, interestId, user.getId()))
            .thenThrow(new InterestNotFoundException(interestId));

        //when & then
        mockMvc.perform(patch("/api/interests/{interestId}", interestId)
                .header("Monew-Request-User-Id", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
            ).andExpect(status().isNotFound())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("INTEREST_NOT_FOUND"))
            .andExpect(jsonPath("$.exceptionType").value("InterestNotFoundException"))
            .andExpect(jsonPath("$.message").value("해당 관심사가 존재하지 않습니다."))
            .andExpect(jsonPath("$.details").exists())
            .andExpect(jsonPath("$.details.id").value(interestId.toString()));
    }

    @Test
    void subscription() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID interestId = UUID.randomUUID();
        String name = "채소";
        List<String> keywords = List.of("시금치");

        SubscriptionDto subscriptionDto = new SubscriptionDto(
            UUID.randomUUID(),
            interestId,
            name,
            keywords,
            2,
            Instant.now()
        );

        when(subscriptionService.subscription(interestId, userId))
            .thenReturn(subscriptionDto);

        //when & then
        mockMvc.perform(post("/api/interests/{interestId}/subscriptions", interestId)
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.interestId").value(interestId.toString()))
            .andExpect(jsonPath("$.interestName").value(name))
            .andExpect(jsonPath("$.interestKeywords[0]").value(keywords.get(0)))
            .andExpect(jsonPath("$.subscriberCount").value(subscriptionDto.subscriberCount()));

    }

    @Test
    @DisplayName("관심사 목록 조회 API 테스트")
    void testFindAll() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        List<InterestDto> content = List.of(
            new InterestDto(UUID.randomUUID(), "테크놀로지", List.of("기술", "개발"), 1L, false),
            new InterestDto(UUID.randomUUID(), "경제", List.of("주식", "금융"), 1L, false)
        );

        CursorPageResponseInterestDto responseDto = new CursorPageResponseInterestDto(content, null,
            null, 10, 2, false);

        when(interestService.findAll(any(), any())).thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/interests")
                .header("Monew-Request-User-Id", userId.toString())
                .param("keyword", "tech")
                .param("orderBy", "name")
                .param("direction", "ASC")
                .param("limit", "10")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].name").value("테크놀로지"))
            .andExpect(jsonPath("$.hasNext").value(false));

        // verify
        verify(interestService).findAll(eq(userId),
            any(CursorPageRequestInterestDto.class));
    }
}

