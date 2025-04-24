package com.codeit.team2.monew.module.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.notification.service.NotificationService;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceImplTest {

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

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
    @DisplayName("댓글 종아요 - 성공")
    void like_Success() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);
        when(commentLikeRepository.save(any(CommentLike.class))).thenReturn(commentLike);

        // when
        CommentLike result = commentLikeService.like(commentId, userId);

        assertThat(result).isEqualTo(commentLike);
        verify(commentRepository).findById(commentId);
        verify(userRepository).findById(userId);
        verify(commentLikeRepository).existsByCommentIdAndUserId(commentId, userId);
        verify(commentLikeRepository).save(any(CommentLike.class));
        verify(comment).incrementLikeCount();
    }

    @Test
    @DisplayName("댓글 종아요 - 실패: 댓글 없음")
    void like_CommentNotFound() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentLikeService.like(commentId, userId))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("댓글 종아요 - 실패: 사용자 없음")
    void like_UserNotFoound() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentLikeService.like(commentId, userId))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("댓글 종아요 - 실패: 이미 좋아요 누름")
    void like_AlreadyLiked() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(true);

        assertThatThrownBy(() -> commentLikeService.like(commentId, userId))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("댓글 좋아요 취소 - 성공")
    void unlike_Success() {
        when(commentLikeRepository.findByCommentIdAndUserId(commentId, userId))
            .thenReturn(Optional.of(commentLike));
        when(commentLike.getComment()).thenReturn(comment);

        commentLikeService.unlike(commentId, userId);

        verify(commentLikeRepository).findByCommentIdAndUserId(commentId, userId);
        verify(commentLikeRepository).delete(commentLike);
        verify(comment).decrementLikeCount();
    }

    @Test
    @DisplayName("댓글 좋아요 취소 - 실패: 좋아요 존재 안함")
    void unlike_NotLiked() {
        when(commentLikeRepository.findByCommentIdAndUserId(commentId, userId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentLikeService.unlike(commentId, userId))
            .isInstanceOf(RuntimeException.class);
    }
}

