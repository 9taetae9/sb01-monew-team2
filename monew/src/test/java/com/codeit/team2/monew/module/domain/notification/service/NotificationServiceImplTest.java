package com.codeit.team2.monew.module.domain.notification.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.notification.dto.CursorPageResponseNotificationDto;
import com.codeit.team2.monew.module.domain.notification.dto.NotificationDto;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.notification.mapper.NotificationMapper;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private NotificationMapper notificationMapper;
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @DisplayName("내 댓글에 좋아요 눌리면 알림 생성 - 성공")
    @Test
    void createCommentNotification() {
        // given
        Comment comment = mock(Comment.class);
        User author = mock(User.class);
        User liker = mock(User.class);
        UUID commentId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID likerId = UUID.randomUUID();
        String likerNickname = "liker";
        when(comment.getId()).thenReturn(commentId);
        when(liker.getNickname()).thenReturn(likerNickname);
        when(author.getId()).thenReturn(authorId);
        when(liker.getId()).thenReturn(likerId);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            ReflectionTestUtils.setField(notification, "id", UUID.randomUUID());
            return notification;
        });
        when(userRepository.existsById(authorId)).thenReturn(true);
        when(userRepository.existsById(likerId)).thenReturn(true);
        when(commentRepository.existsById(commentId)).thenReturn(true);

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

    @DisplayName("내 댓글에 좋아요 눌리면 알림 생성 - 실패: 댓글 검증 실패")
    @Test
    void createCommentNotificationShouldFail1() {
        // given
        UUID commentId = UUID.randomUUID();
        Comment comment = mock(Comment.class);
        User author = mock(User.class);
        User liker = mock(User.class);

        when(comment.getId()).thenReturn(commentId);
        when(commentRepository.existsById(commentId)).thenReturn(false);

        // when & then
        assertThrows(RuntimeException.class,
            () -> notificationService.createCommentNotification(comment, author, liker));
    }

    @DisplayName("내 댓글에 좋아요 눌리면 알림 생성 - 실패: 유저 검증 실패")
    @Test
    void createCommentNotificationShouldFail2() {
        // given
        UUID commentId = UUID.randomUUID();
        Comment comment = mock(Comment.class);
        UUID authorId = UUID.randomUUID();
        User author = mock(User.class);
        User liker = mock(User.class);

        when(comment.getId()).thenReturn(commentId);
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(author.getId()).thenReturn(authorId);
        when(userRepository.existsById(authorId)).thenReturn(false);

        // when & then
        assertThrows(RuntimeException.class,
            () -> notificationService.createCommentNotification(comment, author, liker));
    }

    @DisplayName("구독한 관심사 관련 기사가 등록되면 알림 생성 - 성공")
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

    @DisplayName("알림 확인 - 성공")
    @Test
    void confirmNotification() {
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
        notificationService.confirmNotification(userId, notificationId);

        // then
        assertEquals(true, notification.isConfirmed());
    }

    @DisplayName("알림 확인 - 실패: 유저 검증 실패")
    @Test
    void confirmNotificationShouldFail1() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);
        // when & then
        assertThrows(RuntimeException.class,
            () -> notificationService.confirmNotification(userId, notificationId));
    }

    @DisplayName("알림 확인 - 실패: 알림 검증 실패")
    @Test
    void confirmNotificationShouldFail2() {
        // given
        UUID notificationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
        // when & then
        assertThrows(RuntimeException.class,
            () -> notificationService.confirmNotification(userId, notificationId));
    }

    @DisplayName("알림 확인 - 실패: 알림의 소유자가 아님")
    @Test
    void confirmNotificationShouldFail3() {
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
            () -> notificationService.confirmNotification(userId, notificationId));
    }

    @DisplayName("알림 전체 확인 - 성공")
    @Test
    void confirmAllNotifications() {
        // given
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        when(userRepository.existsById(userId)).thenReturn(true);

        // when
        notificationService.confirmAllNotifications(userId);

        // then
        verify(notificationRepository).confirmAllByUserId(userId);
    }

    @DisplayName("알림 전체 확인 - 실패")
    @Test
    void confirmAllNotificationsShouldFail() {
        // given
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        // when & then
        assertThrows(RuntimeException.class,
            () -> notificationService.confirmAllNotifications(userId));
    }

    @DisplayName("알림 목록 조회 - 성공")
    @Test
    void findAll() {
        // given
        User user = mock(User.class);
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);

        Notification n1 = new Notification(user, "content", UUID.randomUUID(),
            ResourceType.COMMENT);
        Notification n2 = new Notification(user, "content", UUID.randomUUID(),
            ResourceType.COMMENT);
        Page<Notification> pages = new PageImpl<>(List.of(n1, n2));
        when(notificationRepository.findFirstPage(any(), any())).thenReturn(pages);
        when(notificationRepository.countForPagination(userId)).thenReturn(2L);
        when(notificationMapper.toDto(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            NotificationDto notificationDto = new NotificationDto(notification.getId(),
                notification.getCreatedAt(), notification.getUpdatedAt(),
                notification.isConfirmed(), userId, notification.getContent(),
                notification.getResourceType(), notification.getResourceId());
            return notificationDto;
        });
        // when
        CursorPageResponseNotificationDto result = notificationService.findAll(userId, null, null,
            5);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.totalElements());
        assertFalse(result.hasNext());
        assertNull(result.nextCursor());
        assertNull(result.nextCursor());
        verify(notificationRepository).findFirstPage(any(), any());
        verify(notificationRepository).countForPagination(any());
    }
}
