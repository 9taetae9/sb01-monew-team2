package com.codeit.team2.monew.module.domain.notification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.notification.repository.NotificationRepository;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void createCommentNotification() {
        // given
        Comment comment = mock(Comment.class);
        User author = mock(User.class);
        User liker = mock(User.class);
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String likerNickname = "liker";
        when(comment.getId()).thenReturn(commentId);
        when(liker.getNickname()).thenReturn(likerNickname);
        when(author.getId()).thenReturn(authorId);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            ReflectionTestUtils.setField(notification, "id", UUID.randomUUID());
            return notification;
        });

        // when
        Notification
            notification = notificationService.createCommentNotification(comment, author,
            liker);

        // then
        assertNotNull(notification.getId());
        assertEquals("liker님이 나의 댓글을 좋아합니다.", notification.getContent());
        assertEquals(authorId, notification.getUser().getId());
        assertEquals(commentId, notification.getResourceId());
        assertEquals(ResourceType.COMMENT, notification.getResourceType());
    }

    @Test
    void createInterestNotification() {

        // given
        Interest interest = mock(Interest.class);
        UUID interestId = UUID.randomUUID();
        when(interest.getId()).thenReturn(interestId);
        when(interest.getName()).thenReturn("AI");

        User user = mock(User.class);
        Subscription subscription = mock(Subscription.class);
        when(subscription.getUser()).thenReturn(user);

        Article article = mock(Article.class);
        ArticleInterest articleInterest = mock(ArticleInterest.class);
        when(articleInterest.getInterest()).thenReturn(interest);
        article.getArticleInterests().add(articleInterest);
        when(article.getArticleInterests()).thenReturn(Set.of(articleInterest));
        when(articleInterest.getInterest()).thenReturn(interest);

        when(subscriptionRepository.findAllByInterest(interest))
            .thenReturn(List.of(subscription));
        when(notificationRepository.saveAll(anyList()))
            .thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 알림 그대로 리턴

        // when
        List<Notification> result = notificationService.createInterestNotification(
            List.of(article));

        // then
        assertEquals(1, result.size());
        Notification notification = result.get(0);
        assertEquals(user, notification.getUser());
        assertTrue(notification.getContent().contains("AI"));
        verify(subscriptionRepository, times(1)).findAllByInterest(interest);
        verify(notificationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void readNotification() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        Notification notification = new Notification(user, "cotent", UUID.randomUUID(),
            ResourceType.COMMENT);
        ReflectionTestUtils.setField(notification, "id", notificationId);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notification.getUser().getId()).thenReturn(userId);

        // when
        notificationService.readNotification(userId, notificationId);

        // then
        assertEquals(true, notification.isConfirmed());
    }

    @Test
    void readNotificationShouldFail() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        Notification notification = new Notification(user, "cotent", UUID.randomUUID(),
            ResourceType.COMMENT);
        ReflectionTestUtils.setField(notification, "id", notificationId);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notification.getUser().getId()).thenReturn(UUID.randomUUID());

        // when & then
        assertThrows(RuntimeException.class,
            () -> notificationService.readNotification(userId, notificationId));
    }

    @Test
    void readAllNotifications() {
        // given
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        when(userRepository.existsById(userId)).thenReturn(true);

        // when
        notificationService.readAllNotifications(userId);

        // then
        verify(notificationRepository).confirmAllByUserId(userId);
    }

    @Test
    void readAllNotificationsShouldFail() {
        // given
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        // when & then
        assertThrows(RuntimeException.class,
            () -> notificationService.readAllNotifications(userId));
    }
}
