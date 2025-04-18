package com.codeit.team2.monew.module.domain.notification.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.util.List;

public interface NotificationService {

    // 내 댓글에 좋아요가 눌린 경우 알림 생성
    Notification createCommentNotification(Comment comment, User author, User liker);

    // 구독한 관심사와 관련된 기사가 새로 등록된 경우
    List<Notification> createInterestNotification(List<Article> articles);
}
