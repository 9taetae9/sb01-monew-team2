package com.codeit.team2.monew.module.domain.useractivity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.useractivity.document.CommentItem;
import com.codeit.team2.monew.module.domain.useractivity.document.UserActivity;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.mapper.UserActivityMapper;
import com.codeit.team2.monew.module.domain.useractivity.repository.MongoCommentItemRepository;
import com.codeit.team2.monew.module.domain.useractivity.repository.MongoUserActivityRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MongoUserActivityServiceTest {

    @Mock
    private MongoUserActivityRepository userActivityRepository;

    @Mock
    private MongoCommentItemRepository commentItemRepository;

    @Spy
    private UserActivityMapper userActivitiesMapper = Mappers.getMapper(
        UserActivityMapper.class);

    @InjectMocks
    private MongoUserActivityService userActivityService;

    @Nested
    class findUserActivitiesTest {

        @Test
        void 활동_내역_관리_조회_성공() {
            // given
            UUID userId = UUID.randomUUID();
            UUID loginId = userId;

            String email = "email";
            String nickname = "nickname";
            Instant createdAt = Instant.now();

            UserActivity userActivity = new UserActivity(
                userId,
                email,
                nickname,
                createdAt,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
            );

            when(userActivityRepository.findById(userId)).thenReturn(Optional.of(userActivity));

            // when
            UserActivityDto userActivityDto =
                userActivityService.findUserActivities(loginId, userId);

            // then
            assertEquals(userId, userActivityDto.id());
            assertEquals(email, userActivityDto.email());
            assertEquals(nickname, userActivityDto.nickname());
            assertEquals(createdAt, userActivityDto.createdAt());
        }

        @Test
        void 본인_검증_예외() {
            // given
            UUID userId = UUID.randomUUID();
            UUID loginId = UUID.randomUUID();

            // when & then
            assertThrows(Exception.class, () -> {
                userActivityService.findUserActivities(loginId, userId);
            });
        }
    }

    @Test
    void 사용자_활동_내역_초기_생성_성공() {
        // given
        String email = "email";
        String nickname = "nickname";
        String password = "password";

        User user = new User(email, nickname, password, false);

        // when
        userActivityService.createUserActivity(user);

        // then
        ArgumentCaptor<UserActivity> captor = ArgumentCaptor.forClass(UserActivity.class);
        verify(userActivityRepository).save(captor.capture());

        UserActivity saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getNickname()).isEqualTo(nickname);
        assertThat(saved.getSubscriptions()).isEmpty();
        assertThat(saved.getComments()).isEmpty();
        assertThat(saved.getCommentLikes()).isEmpty();
        assertThat(saved.getArticleViews()).isEmpty();
    }

    @Test
    void CommentItem_추가_성공() {
        // given
        User user = mock(User.class);
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);
        UserActivity userActivity = mock(UserActivity.class);

        UUID commentId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();
        String articleTitle = "title";
        UUID userId = UUID.randomUUID();
        String userNickname = "nickname";
        String content = "content";
        Long likeCount = 0L;
        Instant createdAt = Instant.now();

        // comment
        when(comment.getId()).thenReturn(commentId);
        when(comment.getContent()).thenReturn(content);
        when(comment.getLikeCount()).thenReturn(likeCount);
        when(comment.getCreatedAt()).thenReturn(createdAt);

        // article
        when(article.getId()).thenReturn(articleId);
        when(article.getTitle()).thenReturn(articleTitle);

        // user
        when(user.getId()).thenReturn(userId);
        when(user.getNickname()).thenReturn(userNickname);

        when(userActivityRepository.findById(any())).thenReturn(Optional.of(userActivity));

        // when
        userActivityService.createCommentItem(comment, article, user);

        // then
        ArgumentCaptor<CommentItem> captor = ArgumentCaptor.forClass(CommentItem.class);
        verify(commentItemRepository).save(captor.capture());

        CommentItem saved = captor.getValue();
        assertEquals(commentId, saved.getId());
        assertEquals(articleId, saved.getArticleId());
        assertEquals(articleTitle, saved.getArticleTitle());
        assertEquals(userId, saved.getUserId());
        assertEquals(userNickname, saved.getUserNickname());
        assertEquals(content, saved.getContent());
        assertEquals(likeCount, saved.getLikeCount());
        assertEquals(createdAt, saved.getCreatedAt());
    }

}
