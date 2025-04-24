package com.codeit.team2.monew.module.domain.interest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.service.InterestService;
import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
import com.codeit.team2.monew.module.domain.subscription.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
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
            .header("MoNew-Request-User-ID", userId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto))
        ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.keywords[0]").value(keywords.get(0)))
            .andExpect(jsonPath("$.subscriberCount").value(interestDto.subscriberCount()))
            .andExpect(jsonPath("$.subscribedByMe").value(interestDto.subscribedByMe()));

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
                .header("MoNew-Request-User-ID", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.keywords[0]").value(keywords.get(0)))
            .andExpect(jsonPath("$.subscriberCount").value(interestDto.subscriberCount()))
            .andExpect(jsonPath("$.subscribedByMe").value(interestDto.subscribedByMe()));
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
                .header("MoNew-Request-User-ID", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.interestId").value(interestId.toString()))
            .andExpect(jsonPath("$.interestName").value(name))
            .andExpect(jsonPath("$.interestKeywords[0]").value(keywords.get(0)))
            .andExpect(jsonPath("$.subscriberCount").value(subscriptionDto.subscriberCount()));

    }

    @Test
    void delete() {
    }
}
