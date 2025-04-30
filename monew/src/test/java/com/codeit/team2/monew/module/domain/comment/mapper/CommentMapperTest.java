package com.codeit.team2.monew.module.domain.comment.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.dto.CommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CommentLikeDto;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;


class CommentMapperTest {

    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Test
    @DisplayName("Comment 엔티티를 CommentDto로 변환 - 좋아요 없음")
    void toDto_Comment_NotLiked() {
        // given
        UUID commentId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String nickname = "testUser";
        String content = "Test comment";
        long likeCount = 5L;
        Instant createdAt = Instant.now();
        boolean likedByMe = false;

        Article article = mock(Article.class);
        when(article.getId()).thenReturn(articleId);

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getNickname()).thenReturn(nickname);

        Comment comment = mock(Comment.class);
        when(comment.getId()).thenReturn(commentId);
        when(comment.getArticle()).thenReturn(article);
        when(comment.getUser()).thenReturn(user);
        when(comment.getContent()).thenReturn(content);
        when(comment.getLikeCount()).thenReturn(likeCount);
        when(comment.getCreatedAt()).thenReturn(createdAt);

        // when
        CommentDto result = commentMapper.toDto(comment, likedByMe);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(commentId);
        assertThat(result.articleId()).isEqualTo(articleId);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.userNickname()).isEqualTo(nickname);
        assertThat(result.content()).isEqualTo(content);
        assertThat(result.likeCount()).isEqualTo(likeCount);
        assertThat(result.likedByMe()).isEqualTo(likedByMe);
        assertThat(result.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Comment 엔티티를 CommentDto로 변환 - 좋아요 있음")
    void toDto_Comment_Liked() {
        // given
        UUID commentId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String nickname = "testUser";
        String content = "Test comment";
        long likeCount = 5L;
        Instant createdAt = Instant.now();
        boolean likedByMe = true;

        Article article = mock(Article.class);
        when(article.getId()).thenReturn(articleId);

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getNickname()).thenReturn(nickname);

        Comment comment = mock(Comment.class);
        when(comment.getId()).thenReturn(commentId);
        when(comment.getArticle()).thenReturn(article);
        when(comment.getUser()).thenReturn(user);
        when(comment.getContent()).thenReturn(content);
        when(comment.getLikeCount()).thenReturn(likeCount);
        when(comment.getCreatedAt()).thenReturn(createdAt);

        // when
        CommentDto result = commentMapper.toDto(comment, likedByMe);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(commentId);
        assertThat(result.articleId()).isEqualTo(articleId);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.userNickname()).isEqualTo(nickname);
        assertThat(result.content()).isEqualTo(content);
        assertThat(result.likeCount()).isEqualTo(likeCount);
        assertThat(result.likedByMe()).isEqualTo(likedByMe);
        assertThat(result.createdAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("CommentLike 엔티티를 CommentLikeDto로 변환")
    void toDto_CommentLike() {
        // given
        UUID commentLikeId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID commentUserId = UUID.randomUUID();
        String commentUserNickname = "commentWriter";
        String commentContent = "Comment content";
        long commentLikeCount = 10L;
        Instant commentCreatedAt = Instant.now().minusSeconds(3600); //댓글 생성 시간(1시간 전)
        Instant likedAt = Instant.now(); // 좋아요 누른 시점(댓글 달린 1시간 후)

        Article article = mock(Article.class);
        when(article.getId()).thenReturn(articleId);

        User commentUser = mock(User.class);
        when(commentUser.getId()).thenReturn(commentUserId);
        when(commentUser.getNickname()).thenReturn(commentUserNickname);

        User likeUser = mock(User.class);
        when(likeUser.getId()).thenReturn(userId);

        Comment comment = mock(Comment.class);
        when(comment.getId()).thenReturn(commentId);
        when(comment.getArticle()).thenReturn(article);
        when(comment.getUser()).thenReturn(commentUser);
        when(comment.getContent()).thenReturn(commentContent);
        when(comment.getLikeCount()).thenReturn(commentLikeCount);
        when(comment.getCreatedAt()).thenReturn(commentCreatedAt);

        CommentLike commentLike = mock(CommentLike.class);
        when(commentLike.getId()).thenReturn(commentLikeId);
        when(commentLike.getComment()).thenReturn(comment);
        when(commentLike.getUser()).thenReturn(likeUser);
        when(commentLike.getLikedAt()).thenReturn(likedAt);

        // when
        CommentLikeDto result = commentMapper.toDto(commentLike);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(commentLikeId);
        assertThat(result.likedBy()).isEqualTo(userId);
        assertThat(result.createdAt()).isEqualTo(likedAt);
        assertThat(result.commentId()).isEqualTo(commentId);
        assertThat(result.articleId()).isEqualTo(articleId);
        assertThat(result.commentUserId()).isEqualTo(commentUserId);
        assertThat(result.commentUserNickname()).isEqualTo(commentUserNickname);
        assertThat(result.commentContent()).isEqualTo(commentContent);
        assertThat(result.commentLikeCount()).isEqualTo(commentLikeCount);
        assertThat(result.commentCreatedAt()).isEqualTo(commentCreatedAt);
    }

    @Test
    @DisplayName("CommentRegisterRequest, Article, User를 Comment 엔티티로 변환")
    void toEntity() {
        // given
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String content = "New comment content";

        Article article = mock(Article.class);
        when(article.getId()).thenReturn(articleId);

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        CommentRegisterRequest request = new CommentRegisterRequest(articleId, userId, content);

        // when
        Comment result = commentMapper.toEntity(request, article, user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getArticle()).isEqualTo(article);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getLikeCount()).isEqualTo(0L);
        assertThat(result.isDeleted()).isFalse();
    }
}
