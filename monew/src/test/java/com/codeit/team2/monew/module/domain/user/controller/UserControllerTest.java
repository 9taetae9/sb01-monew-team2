package com.codeit.team2.monew.module.domain.user.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.team2.monew.module.domain.user.dto.request.UserLoginRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserUpdateRequest;
import com.codeit.team2.monew.module.domain.user.dto.response.UserDto;
import com.codeit.team2.monew.module.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void 사용자_등록_성공() throws Exception {
        // given
        String email = "email@a.com";
        String nickname = "nickname1";
        String password = "password";
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(email, nickname,
            password);

        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        UserDto userDto = new UserDto(userId, email, nickname, createdAt);
        when(userService.registerUser(userRegisterRequest)).thenReturn(userDto);

        // when & then
        mockMvc.perform(
                post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRegisterRequest))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.nickname").value(nickname))
            .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));
    }

    @Test
    void 사용자_수정_성공() throws Exception {
        // given
        String email = "email@a.com";
        String nickname = "newNickname";
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(nickname);

        UUID userId = UUID.randomUUID();
        UUID loginId = userId;
        Instant createdAt = Instant.now();
        UserDto userDto = new UserDto(userId, email, nickname, createdAt);
        when(userService.updateUser(loginId, userId, userUpdateRequest)).thenReturn(userDto);

        // when & then
        mockMvc.perform(
                patch("/api/users/{userId}", userId)
                    .header("Monew-Request-User-Id", loginId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userUpdateRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.nickname").value(nickname))
            .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));
    }

    @Test
    void 사용자_로그인_성공() throws Exception {
        // given
        String email = "email@a.com";
        String nickname = "nickname1";
        String password = "password";
        UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);

        UUID userId = UUID.randomUUID();
        UUID loginId = userId;
        Instant createdAt = Instant.now();
        UserDto userDto = new UserDto(userId, email, nickname, createdAt);
        when(userService.login(userLoginRequest)).thenReturn(userDto);

        // when & then
        mockMvc.perform(
                post("/api/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userLoginRequest))
            )
            .andExpect(status().isOk())
            .andExpect(header().string("Monew-Request-User-Id", userId.toString()))
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.nickname").value(nickname))
            .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));
    }
}
