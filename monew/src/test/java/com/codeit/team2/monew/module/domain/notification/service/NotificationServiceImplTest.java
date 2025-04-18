package com.codeit.team2.monew.module.domain.notification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.notification.repository.NotificationRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
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
//        // given
//        List<Article> articles = List.of(mock(Article.class));
//        when(notificationRepository.saveAll(anyList())).thenAnswer(
//            invocation -> invocation.getArgument(0));
//
//        // when
//        List<Notification> notifications = notificationService.createInterestNotification(articles);
//
//        // then
//        assertNotNull(notifications);
//        assertEquals(1, notifications.size());
//        Notification notification = notifications.get(0);
//        assertNotNull(notification.getUser());
//        verify(notificationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void readNotification() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        ReflectionTestUtils.setField(user, "id", userId);
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
        ReflectionTestUtils.setField(user, "id", userId);
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
        ReflectionTestUtils.setField(user, "id", userId);

        Notification notification1 = new Notification(user, "cotent", UUID.randomUUID(),
            ResourceType.COMMENT);
        Notification notification2 = new Notification(user, "cotent", UUID.randomUUID(),
            ResourceType.COMMENT);
        Notification notification3 = new Notification(user, "cotent", UUID.randomUUID(),
            ResourceType.COMMENT);

        List<Notification> notifications = List.of(notification1, notification2, notification3);

        // when
        notificationService.readAllNotification(userId);

        // then
        assertEquals(true, notification1.isConfirmed());
        assertEquals(true, notification2.isConfirmed());
        assertEquals(true, notification3.isConfirmed());
    }
}
