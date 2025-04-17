package com.codeit.team2.monew.module.domain.notification.service;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.member.entity.User;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository notificationRepository;

    @Override
    public Notification createCommentNotification(Comment comment, User author, User liker) {
        return null;
    }

    @Override
    public Notification createInterestNotification() {
        return null;
    }
}
