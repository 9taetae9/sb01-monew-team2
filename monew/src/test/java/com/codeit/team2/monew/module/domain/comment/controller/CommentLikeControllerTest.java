package com.codeit.team2.monew.module.domain.comment.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.team2.monew.module.common.exception.GlobalExceptionHandler;
import com.codeit.team2.monew.module.domain.comment.dto.CommentLikeDto;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.exception.CommentLikeAlreadyExistsException;
import com.codeit.team2.monew.module.domain.comment.exception.CommentLikeNotFoundException;
import com.codeit.team2.monew.module.domain.comment.exception.CommentNotFoundException;
import com.codeit.team2.monew.module.domain.comment.mapper.CommentMapper;
import com.codeit.team2.monew.module.domain.comment.service.CommentLikeService;
import com.codeit.team2.monew.module.domain.user.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class CommentLikeControllerTest {

    @Mock
    private CommentLikeService commentLikeService;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentLikeController commentLikeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID articleId;
    private UUID commentId;
    private UUID commentLikeId;
    private CommentLikeDto commentLikeDto;
    private CommentLike commentLike;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(
            objectMapper);
        converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));

        mockMvc = MockMvcBuilders.standaloneSetup(commentLikeController)
            .setMessageConverters(converter)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        userId = UUID.randomUUID();
        articleId = UUID.randomUUID();
        commentId = UUID.randomUUID();
        commentLikeId = UUID.randomUUID();

        commentLikeDto = new CommentLikeDto(
            commentLikeId,
            userId,
            Instant.now(),
            commentId,
            articleId,
            userId,
            "testUser",
            "Test comment content",
            1L,
            Instant.now()
        );

        commentLike = mock(CommentLike.class);
    }

    @Test
    @DisplayName("댓글 좋아요 등록 - 성공")
    void like_Success() throws Exception {
        // given
        when(commentLikeService.like(eq(commentId), eq(userId))).thenReturn(commentLike);
        when(commentMapper.toDto(commentLike)).thenReturn(commentLikeDto);

        // when & then
        mockMvc.perform(post("/api/comments/{commentId}/comment-likes", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(commentLikeDto)));
    }

    @Test
    @DisplayName("댓글 좋아요 등록 - 실패: 댓글 없음")
    void like_CommentNotFound() throws Exception {
        // given
        when(commentLikeService.like(eq(commentId), eq(userId)))
            .thenThrow(new CommentNotFoundException(commentId));

        // when & then
        mockMvc.perform(post("/api/comments/{commentId}/comment-likes", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 좋아요 등록 - 실패: 사용자 없음")
    void like_UserNotFound() throws Exception {
        // given
        when(commentLikeService.like(eq(commentId), eq(userId)))
            .thenThrow(new UserNotFoundException(userId));

        // when & then
        mockMvc.perform(post("/api/comments/{commentId}/comment-likes", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 좋아요 등록 - 실패: 이미 좋아요 존재")
    void like_AlreadyExists() throws Exception {
        // given
        when(commentLikeService.like(eq(commentId), eq(userId)))
            .thenThrow(new CommentLikeAlreadyExistsException(commentId, userId));

        // when & then
        mockMvc.perform(post("/api/comments/{commentId}/comment-likes", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("댓글 좋아요 취소 - 성공")
    void unlike_Success() throws Exception {
        // given
        doNothing().when(commentLikeService).unlike(commentId, userId);

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}/comment-likes", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 좋아요 취소 - 실패: 좋아요 없음")
    void unlike_NotFound() throws Exception {
        // given
        doThrow(new CommentLikeNotFoundException(commentId, userId))
            .when(commentLikeService).unlike(commentId, userId);

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}/comment-likes", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isNotFound());
    }
}
