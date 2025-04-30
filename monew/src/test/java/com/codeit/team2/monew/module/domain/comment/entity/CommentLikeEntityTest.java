package com.codeit.team2.monew.module.domain.comment.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentLikeEntityTest {

    @Test
    @DisplayName("CommentLike 엔티티 생성 - create 정적 메서드")
    void create() {
        // given
        Comment comment = mock(Comment.class);
        User user = mock(User.class);

        Instant beforeCreation = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        // when
        CommentLike commentLike = CommentLike.create(comment, user);

        // 좋아요 엔티티 likedAt 값은 좋아요 누르기 이전 시점과 좋아요 누른 직후 시점 사이어야 함
        Instant likedAtTruncated = commentLike.getLikedAt().truncatedTo(ChronoUnit.MILLIS);

        Instant afterCreation = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        // then
        assertThat(commentLike).isNotNull();
        assertThat(commentLike.getComment()).isEqualTo(comment);
        assertThat(commentLike.getUser()).isEqualTo(user);

        // likedAt이 현재 시간과 가까운지 확인 (단위: ms)
        assertThat(likedAtTruncated).isNotNull();
        assertThat(likedAtTruncated).isBetween(beforeCreation, afterCreation);
    }

    @Test
    @DisplayName("CommentLike 엔티티 - 저장 시 식별자 생성 확인")
    void idGeneration() {
        // given
        Comment comment = mock(Comment.class);
        User user = mock(User.class);
        CommentLike commentLike = CommentLike.create(comment, user);

        // commentLike 저장 전 id - null
        assertThat(commentLike.getId()).isNull();

        CommentLikeRepository mockRepository = mock(CommentLikeRepository.class);
        when(mockRepository.save(any(CommentLike.class))).thenAnswer(invocation -> {
            CommentLike savedCommentLike = invocation.getArgument(0);

            // 영속화 시점 - id 생성
            Field idField = savedCommentLike.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedCommentLike, UUID.randomUUID());

            return savedCommentLike;
        });

        // when
        CommentLike savedCommentLike = mockRepository.save(commentLike);

        // then
        assertThat(savedCommentLike.getId()).isNotNull();
        assertThat(savedCommentLike).isSameAs(commentLike); // 동일 인스턴스인지 확인
    }
}
