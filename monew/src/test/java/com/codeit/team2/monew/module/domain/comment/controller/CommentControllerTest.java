package com.codeit.team2.monew.module.domain.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.team2.monew.module.common.exception.GlobalExceptionHandler;
import com.codeit.team2.monew.module.domain.article.exception.ArticleNotFoundException;
import com.codeit.team2.monew.module.domain.comment.dto.CommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CommentUpdateRequest;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.exception.CommentNotFoundException;
import com.codeit.team2.monew.module.domain.comment.exception.CommentPermissionDeniedException;
import com.codeit.team2.monew.module.domain.comment.mapper.CommentMapper;
import com.codeit.team2.monew.module.domain.comment.service.CommentService;
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
public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentController commentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID articleId;
    private UUID commentId;
    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));

        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
            .setMessageConverters(converter)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        userId = UUID.randomUUID();
        articleId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        commentDto = new CommentDto(
            commentId,
            articleId,
            userId,
            "testUser",
            "Test comment content",
            0L,
            false,
            Instant.now()
        );

        comment = mock(Comment.class);
    }

    @Test
    @DisplayName("댓글 등록 - 성공")
    void register_Success() throws Exception {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(
            articleId,
            userId,
            "Test comment content"
        );

        when(commentService.register(any(CommentRegisterRequest.class))).thenReturn(comment);
        when(commentMapper.toDto(comment, false)).thenReturn(commentDto);

        // when & then
        mockMvc.perform(post("/api/comments")
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    @DisplayName("댓글 등록 - 실패: 유효하지 않은 입력")
    void register_BadRequest() throws Exception {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(
            articleId,
            userId,
            ""
        );

        // when & then
        mockMvc.perform(post("/api/comments")
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 등록 - 실패: 사용자 없음")
    void register_UserNotFound() throws Exception {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(
            articleId,
            userId,
            "Test comment content"
        );

        when(commentService.register(any(CommentRegisterRequest.class)))
            .thenThrow(new UserNotFoundException(userId));

        // when & then
        mockMvc.perform(post("/api/comments")
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 등록 - 실패: 기사 없음")
    void register_ArticleNotFound() throws Exception {
        // given
        CommentRegisterRequest request = new CommentRegisterRequest(
            articleId,
            userId,
            "Test comment content"
        );

        when(commentService.register(any(CommentRegisterRequest.class)))
            .thenThrow(new ArticleNotFoundException(articleId));

        // when & then
        mockMvc.perform(post("/api/comments")
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 수정 - 성공")
    void update_Success() throws Exception {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest("Updated content");
        CommentDto updatedDto = new CommentDto(
            commentId,
            articleId,
            userId,
            "testUser",
            "Updated content",
            0L,
            false,
            Instant.now()
        );

        when(commentService.edit(eq(commentId), eq(userId), any(CommentUpdateRequest.class)))
            .thenReturn(updatedDto);

        // when & then
        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(updatedDto)));
    }

    @Test
    @DisplayName("댓글 수정 - 실패: 권한 없음")
    void update_Forbidden() throws Exception {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest("Updated content");

        when(commentService.edit(eq(commentId), eq(userId), any(CommentUpdateRequest.class)))
            .thenThrow(new CommentPermissionDeniedException(commentId, userId));

        // when & then
        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글 수정 - 실패: 댓글 없음")
    void update_NotFound() throws Exception {
        // given
        CommentUpdateRequest request = new CommentUpdateRequest("Updated content");

        when(commentService.edit(eq(commentId), eq(userId), any(CommentUpdateRequest.class)))
            .thenThrow(new CommentNotFoundException(commentId));

        // when & then
        mockMvc.perform(patch("/api/comments/{commentId}", commentId)
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    void delete_Success() throws Exception {
        // given
        doNothing().when(commentService).delete(commentId, userId);

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 삭제 - 실패: 권한 없음")
    void delete_Forbidden() throws Exception {
        // given
        doThrow(new CommentPermissionDeniedException(commentId, userId))
            .when(commentService).delete(commentId, userId);

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("댓글 삭제 - 실패: 댓글 없음")
    void delete_NotFound() throws Exception {
        // given
        doThrow(new CommentNotFoundException(commentId))
            .when(commentService).delete(commentId, userId);

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 물리 삭제 - 성공")
    void hardDelete_Success() throws Exception {
        // given
        doNothing().when(commentService).hardDelete(commentId);

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("댓글 물리 삭제 - 실패: 댓글 없음")
    void hardDelete_NotFound() throws Exception {
        // given
        doThrow(new CommentNotFoundException(commentId))
            .when(commentService).hardDelete(commentId);

        // when & then
        mockMvc.perform(delete("/api/comments/{commentId}/hard", commentId)
                .header("Monew-Request-User-Id", userId.toString()))
            .andExpect(status().isNotFound());
    }
}
