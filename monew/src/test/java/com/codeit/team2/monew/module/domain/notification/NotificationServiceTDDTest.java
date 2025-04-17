package com.codeit.team2.monew.module.domain.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.member.entity.User;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.notification.repository.NotificationRepository;
import com.codeit.team2.monew.module.domain.notification.service.NotificationServiceImpl;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTDDTest {

    @Mock
    private NotificationRepository notificationRepository;

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
        Interest interest = mock(Interest.class);    // 구독한 관심사
        User user = mock(User.class);
        Subscription subscription = mock(Subscription.class);
        ReflectionTestUtils.setField(subscription, "user", user);
        ReflectionTestUtils.setField(subscription, "interest", interest);
        List<Article> articleList = List.of(
            new Article("AI", "NAVER", "https://testlink.com", "test description", 0,
                Instant.now(), false));

        // when
        List<Notification> notifications = notificationService.createInterestNotification(
            articleList);

        // then
        notifications.stream()
            .forEach(notification -> {
                assertNotNull(notification.getId());
            });
    }
}
