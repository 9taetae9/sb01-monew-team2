package com.codeit.team2.monew.module.domain.useractivity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.TestEntityFactory;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.user.TestUserFactory;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.useractivity.document.ArticleViewItem;
import com.codeit.team2.monew.module.domain.useractivity.document.CommentItem;
import com.codeit.team2.monew.module.domain.useractivity.document.CommentLikeItem;
import com.codeit.team2.monew.module.domain.useractivity.document.SubscriptionItem;
import com.codeit.team2.monew.module.domain.useractivity.document.UserActivity;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.mapper.UserActivityMapper;
import com.codeit.team2.monew.module.domain.useractivity.repository.MongoUserActivityRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private CommentRepository commentRepository;

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
        UUID userId = UUID.randomUUID();
        String email = "email";
        String userNickname = "nickname";
        Instant createdAt = Instant.now();

        UUID commentId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();
        String articleTitle = "title";
        String content = "content";
        Long likeCount = 0L;

        User user = mock(User.class);
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);
        CommentItem commentItem = mock(CommentItem.class);
        UserActivity userActivity = new UserActivity(
            userId,
            email,
            userNickname,
            createdAt,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>()
        );

        // comments의 개수를 미리 10개로 채워 놓는다.
        for (int i = 0; i < 10; i++) {
            userActivity.addCommentItem(commentItem);
        }

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
        ArgumentCaptor<UserActivity> userActivityArgumentCaptor = ArgumentCaptor.forClass(
            UserActivity.class);
        verify(userActivityRepository).save(userActivityArgumentCaptor.capture());

        UserActivity userActivitySaved = userActivityArgumentCaptor.getValue();
        assertEquals(userId, userActivitySaved.getId());
        assertEquals(email, userActivitySaved.getEmail());
        assertEquals(userNickname, userActivitySaved.getNickname());
        assertEquals(createdAt, userActivitySaved.getCreatedAt());
        assertEquals(10, userActivitySaved.getComments().size());
    }

    @Nested
    class createSubscriptionItemTest {

        @Test
        void SubscriptionItem_추가_성공() {
            // given
            UUID userId = UUID.randomUUID();
            UUID subscriptionId = UUID.randomUUID();
            UUID interestId = UUID.randomUUID();
            String interestName = "AI";
            List<String> keywords = List.of("chatgpt", "llm");
            Long subscriberCount = 100L;
            Instant createdAt = Instant.now();

            // mocks
            UserActivity userActivity = new UserActivity(
                userId,
                "email@test.com",
                "nickname",
                createdAt,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );

            Subscription subscription = mock(Subscription.class);
            Interest interest = mock(Interest.class);
            InterestKeyword keyword1 = mock(InterestKeyword.class);
            InterestKeyword keyword2 = mock(InterestKeyword.class);
            Keyword k1 = mock(Keyword.class);
            Keyword k2 = mock(Keyword.class);

            when(subscription.getId()).thenReturn(subscriptionId);
            when(subscription.getCreatedAt()).thenReturn(createdAt);

            when(interest.getId()).thenReturn(interestId);
            when(interest.getName()).thenReturn(interestName);
            when(interest.getSubscriberCount()).thenReturn(subscriberCount);
            when(interest.getKeywords()).thenReturn(Set.of(keyword1, keyword2));

            when(keyword1.getKeyword()).thenReturn(k1);
            when(keyword2.getKeyword()).thenReturn(k2);
            when(k1.getName()).thenReturn(keywords.get(0));
            when(k2.getName()).thenReturn(keywords.get(1));

            for (int i = 0; i < 10; i++) {
                userActivity.addSubscriptionItem(mock(SubscriptionItem.class));
            }

            when(userActivityRepository.findById(userId)).thenReturn(Optional.of(userActivity));

            // when
            userActivityService.createSubscriptionItem(subscription, interest, userId);

            // then
            ArgumentCaptor<UserActivity> captor = ArgumentCaptor.forClass(UserActivity.class);
            verify(userActivityRepository).save(captor.capture());

            UserActivity saved = captor.getValue();
            assertEquals(10, saved.getSubscriptions().size());

            SubscriptionItem savedItem = saved.getSubscriptions().get(0);
            assertEquals(subscriptionId, savedItem.getId());
            assertEquals(interestId, savedItem.getInterestId());
            assertEquals(interestName, savedItem.getInterestName());
            assertEquals(keywords, savedItem.getInterestKeywords());
            assertEquals(subscriberCount, savedItem.getInterestSubscriberCount());
            assertEquals(createdAt, savedItem.getCreatedAt());
        }
    }

    @Nested
    class createCommentLikeItemTest {

        @Test
        void CommentLikeItem_추가_성공() {
            // given
            User user = TestUserFactory.createWithName("user1");
            Article article = TestEntityFactory.createArticle("title1");
            Comment comment = TestEntityFactory.createComment(article, user, "comment1");

            UserActivity userActivity = new UserActivity(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );

            CommentItem commentItem = new CommentItem(
                comment.getId(),
                article.getId(),
                article.getTitle(),
                user.getId(),
                user.getNickname(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt()
            );

            CommentItem mockCommentItem = mock(CommentItem.class);
            when(mockCommentItem.getId()).thenReturn(UUID.randomUUID());

            for (int i = 0; i < 4; i++) {
                userActivity.addCommentItem(mockCommentItem);
            }
            userActivity.addCommentItem(commentItem);
            for (int i = 0; i < 5; i++) {
                userActivity.addCommentItem(mockCommentItem);
            }

            CommentLike commentLike = CommentLike.create(comment, user);
            comment.incrementLikeCount();

            for (int i = 0; i < 10; i++) {
                userActivity.addCommentLikeItem(mock(CommentLikeItem.class));
            }

            when(userActivityRepository.findById(any())).thenReturn(Optional.of(userActivity));

            // when
            userActivityService.createCommentLikeItem(commentLike);

            // then
            ArgumentCaptor<UserActivity> captor = ArgumentCaptor.forClass(UserActivity.class);
            verify(userActivityRepository).save(captor.capture());

            UserActivity saved = captor.getValue();
            assertEquals(10, saved.getCommentLikes().size());

            userActivity.getComments().stream()
                .filter(savedCommentItem -> savedCommentItem.getId().equals(comment.getId()))
                .findAny()
                .ifPresent(
                    findedCommentItem -> assertEquals(1, findedCommentItem.getLikeCount()));
        }
    }

    @Nested
    class createArticleViewItemTest {

        @Test
        void ArticleViewItem_추가_성공() {
            // given
            // article
            Article article = mock(Article.class);
            UUID articleId = UUID.randomUUID();
            String source = "NAVER";
            String sourceUrl = "http://example.com";
            String title = "title";
            Instant publishedDate = Instant.now();
            String summary = "summary";
            Long viewCount = 0L;

            when(article.getId()).thenReturn(articleId);
            when(article.getSource()).thenReturn(source);
            when(article.getSourceUrl()).thenReturn(sourceUrl);
            when(article.getTitle()).thenReturn(title);
            when(article.getPublishedDate()).thenReturn(publishedDate);
            when(article.getSummary()).thenReturn(summary);
            when(article.getViewCount()).thenReturn(viewCount);
            when(commentRepository.countByArticle(any())).thenReturn(10L);

            // articleView
            ArticleView articleView = mock(ArticleView.class);
            UUID articleViewId = UUID.randomUUID();
            Instant createdAt = Instant.now();

            when(articleView.getArticle()).thenReturn(article);
            when(articleView.getId()).thenReturn(articleViewId);
            when(articleView.getCreatedAt()).thenReturn(createdAt);

            // user
            User user = mock(User.class);
            UUID userId = UUID.randomUUID();

            when(user.getId()).thenReturn(userId);

            // user activity
            UserActivity userActivity = new UserActivity(
                userId,
                "email@test.com",
                "nickname",
                createdAt,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            );

            for (int i = 0; i < 10; i++) {
                userActivity.addArticleViewItem(mock(ArticleViewItem.class));
            }

            when(userActivityRepository.findById(userId)).thenReturn(Optional.of(userActivity));

            // when
            userActivityService.createArticleViewItem(articleView, user);

            // then
            ArgumentCaptor<UserActivity> captor = ArgumentCaptor.forClass(UserActivity.class);
            verify(userActivityRepository).save(captor.capture());

            UserActivity saved = captor.getValue();
            assertEquals(10, saved.getArticleViews().size());

            ArticleViewItem savedItem = saved.getArticleViews().get(0);
            assertEquals(articleViewId, savedItem.getId());
            assertEquals(userId, savedItem.getViewedBy());
            assertEquals(createdAt, savedItem.getCreatedAt());
            assertEquals(articleId, savedItem.getArticleId());
            assertEquals(source, savedItem.getSource());
            assertEquals(sourceUrl, savedItem.getSourceUrl());
            assertEquals(title, savedItem.getArticleTitle());
            assertEquals(publishedDate, savedItem.getArticlePublishedDate());
            assertEquals(summary, savedItem.getArticleSummary());
            assertEquals(10L, savedItem.getArticleCommentCount());
            assertEquals(viewCount, savedItem.getArticleViewCount());
        }
    }

}
