package com.codeit.team2.monew.module.domain.notification.service;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.member.entity.User;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification createCommentNotification(Comment comment, User author, User liker) {
        Notification notification = new Notification(author, null, comment.getId(),
            ResourceType.COMMENT);
        notificationRepository.save(notification);
        return notification;
    }

    @Override
    public Notification createInterestNotification() {
        return null;
    }
}
