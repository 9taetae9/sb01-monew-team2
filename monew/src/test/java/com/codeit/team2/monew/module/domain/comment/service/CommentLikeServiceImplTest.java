package com.codeit.team2.monew.module.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.comment.dto.CommentLikeDto;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.exception.CommentLikeAlreadyExistsException;
import com.codeit.team2.monew.module.domain.comment.exception.CommentNotFoundException;
import com.codeit.team2.monew.module.domain.comment.mapper.CommentMapper;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.notification.service.NotificationService;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.exception.UserNotFoundException;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceImplTest {

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;
    @Mock
    private NotificationService notificationService;

    @Spy
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private CommentLikeServiceImpl commentLikeService;

    private UUID userId;
    private UUID commentId;
    private User user;
    private Comment comment;
    private CommentLike commentLike;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        user = mock(User.class);
        comment = mock(Comment.class);
        commentLike = mock(CommentLike.class);
    }

    @Test
    @DisplayName("댓글 좋아요 - 성공")
    void like_Success() {
        CommentLikeDto commentLikeDto = new CommentLikeDto(UUID.randomUUID(), userId, Instant.now(),
            commentId, UUID.randomUUID(), UUID.randomUUID(), "nickname", "content", 1L,
            Instant.now());
        when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);
        when(commentLikeRepository.save(any(CommentLike.class))).thenReturn(commentLike);
        when(commentMapper.toDto(any(CommentLike.class))).thenReturn(commentLikeDto);

        // when
        CommentLikeDto result = commentLikeService.like(commentId, userId);

        verify(commentRepository).findByIdAndDeletedFalse(commentId);
        verify(userRepository).findById(userId);
        verify(commentLikeRepository).existsByCommentIdAndUserId(commentId, userId);
        verify(commentLikeRepository).save(any(CommentLike.class));
        verify(comment).incrementLikeCount();
    }

    @Test
    @DisplayName("댓글 좋아요 - 실패: 댓글 없음")
    void like_CommentNotFound() {
        when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentLikeService.like(commentId, userId))
            .isInstanceOf(CommentNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode.httpStatus.value", 404);
    }

    @Test
    @DisplayName("댓글 좋아요 - 실패: 사용자 없음")
    void like_UserNotFoound() {
        when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentLikeService.like(commentId, userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode.httpStatus.value", 404);
    }

    @Test
    @DisplayName("댓글 좋아요 - 실패: 이미 좋아요 누름")
    void like_AlreadyLiked() {
        when(commentRepository.findByIdAndDeletedFalse(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(true);

        assertThatThrownBy(() -> commentLikeService.like(commentId, userId))
            .isInstanceOf(CommentLikeAlreadyExistsException.class)
            .hasFieldOrPropertyWithValue("errorCode.httpStatus.value", 409);
    }


    @Test
    @DisplayName("댓글 좋아요 취소 - 성공")
    void unlike_Success() {
        when(commentLikeRepository.findByCommentIdAndUserId(commentId, userId))
            .thenReturn(Optional.of(commentLike));
        when(commentLike.getComment()).thenReturn(comment);
        when(comment.isDeleted()).thenReturn(false);

        commentLikeService.unlike(commentId, userId);

        verify(commentLikeRepository).findByCommentIdAndUserId(commentId, userId);
        verify(commentLikeRepository).delete(commentLike);
        verify(comment).decrementLikeCount();
    }

    @Test
    @DisplayName("댓글 좋아요 취소 - 실패: 삭제된 댓글")
    void unlike_DeletedComment() {
        when(commentLikeRepository.findByCommentIdAndUserId(commentId, userId))
            .thenReturn(Optional.of(commentLike));
        when(commentLike.getComment()).thenReturn(comment);
        when(comment.isDeleted()).thenReturn(true); // 삭제된 댓글임을 명시

        assertThatThrownBy(() -> commentLikeService.unlike(commentId, userId))
            .isInstanceOf(CommentNotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode.httpStatus.value", 404);
    }
}

