package com.codeit.team2.monew.module.domain.notification.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.member.entity.User;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.notification.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification createCommentNotification(Comment comment, User author, User liker) {
        // Comment, User 검증 로직 추가 예정
        String content = liker.getNickname() + "님이 나의 댓글을 좋아합니다.";
        Notification notification = new Notification(author, content, comment.getId(),
            ResourceType.COMMENT);
        notificationRepository.save(notification);
        return notification;
    }

    @Override
    public List<Notification> createInterestNotification(List<Article> articles) {
        List<Notification> notifications = List.of(
            new Notification(new User("a", "a", "a", false), "content",
                UUID.randomUUID(), ResourceType.INTEREST));
        notificationRepository.saveAll(notifications);
        return notifications;
    }
}
