package com.codeit.team2.monew.module.domain.notification.service;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.notification.dto.CursorPageResponseNotificationDto;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

    // 내 댓글에 좋아요가 눌린 경우 알림 생성
    Notification createCommentNotification(Comment comment, User author, User liker);

    // 구독한 관심사와 관련된 기사가 새로 등록된 경우
    List<Notification> createArticleInterestNotification(List<ArticleInterest> articleInterests);

    // 개별 알림 확인
    void confirmNotification(UUID userID, UUID notificationId);

    // 전체 알림 확인
    void confirmAllNotifications(UUID userId);

    // 알림 목록 조회 - 커서페이지네이션
    CursorPageResponseNotificationDto findAll(UUID userId, Instant cursor, Instant after,
        int limit);
}
