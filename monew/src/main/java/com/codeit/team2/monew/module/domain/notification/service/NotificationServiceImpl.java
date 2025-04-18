package com.codeit.team2.monew.module.domain.notification.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.member.entity.User;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.notification.repository.NotificationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
        List<Notification> notifications = new ArrayList<>();
//        // TODO: Article 엔티티와 SubscriptionRepository가 필요
//
//        Map<Interest, List<Article>> groupedByInterest = articles.stream()
//            .collect(Collectors.groupingBy(Article::getInterest));
//
//        groupedByInterest.keySet().stream()
//            .forEach(interest -> {
//                String content =
//                    interest.getName() + "와 관련된 기사가 " + groupedByInterest.get(interest).size()
//                        + "건 등록되었습니다.";
//
//                List<Subscription> subscriptions = subscriptionRepository.findByInterest(
//                    interest);
//                List<User> subscriptingUsers = subscriptions.stream()
//                    .map(subscription -> subscription.getUser())
//                    .collect(Collectors.toCollection());
//                subscriptingUsers.stream()
//                    .forEach(user -> {
//                        notifications.add(new Notification(user, content, interest.getId(),
//                            ResourceType.INTEREST));
//                    });
//            });
//        notificationRepository.saveAll(notifications);
        return notifications;
    }

    @Override
    public void readNotification(UUID userId, UUID notificationId) {
        // 사용자 정보 확인
//        if (!userRepository.existsById(userId)) {
//            throw new NoSuchElementException();
//        }
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NoSuchElementException());

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException();
        }
        
        notification.confirmNotification();
    }
}
