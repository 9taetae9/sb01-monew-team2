package com.codeit.team2.monew.module.domain.comment.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentEntityTest {

    private Article article;
    private User user;
    private String content;

    @BeforeEach
    void setUp() {
        article = mock(Article.class);
        user = mock(User.class);
        content = "Test comment content";
    }

    @Test
    @DisplayName("Comment 엔티티 생성 - create 정적 메서드")
    void create() {
        // when
        Comment comment = Comment.create(article, user, content);

        // then
        assertThat(comment).isNotNull();
        assertThat(comment.getArticle()).isEqualTo(article);
        assertThat(comment.getUser()).isEqualTo(user);
        assertThat(comment.getContent()).isEqualTo(content);
        assertThat(comment.getLikeCount()).isEqualTo(0L);
        assertThat(comment.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("Comment 내용 업데이트")
    void update() {
        // given
        Comment comment = Comment.create(article, user, content);
        String updatedContent = "Updated content";

        // when
        comment.update(updatedContent);

        // then
        assertThat(comment.getContent()).isEqualTo(updatedContent);
    }

    @Test
    @DisplayName("Comment 논리 삭제")
    void delete() {
        // given
        Comment comment = Comment.create(article, user, content);
        assertThat(comment.isDeleted()).isFalse();

        // when
        comment.delete();

        // then
        assertThat(comment.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Comment 좋아요 수 증가")
    void incrementLikeCount() {
        // given
        Comment comment = Comment.create(article, user, content);
        assertThat(comment.getLikeCount()).isEqualTo(0L);

        // when
        comment.incrementLikeCount();

        // then
        assertThat(comment.getLikeCount()).isEqualTo(1L);

        // when - 2번째 증가
        comment.incrementLikeCount();

        // then
        assertThat(comment.getLikeCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Comment 좋아요 수 감소")
    void decrementLikeCount() {
        // given
        Comment comment = Comment.create(article, user, content);
        comment.incrementLikeCount();
        comment.incrementLikeCount();
        assertThat(comment.getLikeCount()).isEqualTo(2L);

        // when
        comment.decrementLikeCount();

        // then
        assertThat(comment.getLikeCount()).isEqualTo(1L);

        // when - 2번째 감소
        comment.decrementLikeCount();

        // then
        assertThat(comment.getLikeCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("좋아요 수 0 이하로 감소 x")
    void decrementLikeCount_NotBelowZero() {
        // given
        Comment comment = Comment.create(article, user, content);
        assertThat(comment.getLikeCount()).isEqualTo(0L);

        // when
        comment.decrementLikeCount();

        // then
        assertThat(comment.getLikeCount()).isEqualTo(0L);
    }
}
