package com.codeit.team2.monew.module.domain.notification.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.notification.repository.NotificationRepository;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Notification createCommentNotification(Comment comment, User author, User liker) {
        // Comment 검증 로직 추가 예정
        if (!userRepository.existsById(author.getId()) || !userRepository.existsById(
            liker.getId())) {
            throw new RuntimeException("User or Comment Not Found");
        }
        String content = liker.getNickname() + "님이 나의 댓글을 좋아합니다.";
        Notification notification = new Notification(author, content, comment.getId(),
            ResourceType.COMMENT);
        notificationRepository.save(notification);
        return notification;
    }

    @Transactional
    @Override
    public List<Notification> createInterestNotification(List<Article> articles) {
        List<Notification> notifications = new ArrayList<>();

        // 관심사별 새로 등록된 기사 수 세기
        Map<Interest, Integer> interestCount = new HashMap<>();

        articles.stream()
            .flatMap(article -> article.getArticleInterests().stream())
            .map(articleInterest -> articleInterest.getInterest())
            .forEach(interest -> {
                interestCount.put(interest, interestCount.getOrDefault(interest, 0) + 1);
            });

        interestCount.keySet().stream()
            .forEach(interest -> {
                String content =
                    interest.getName() + "와 관련된 기사가 " + interestCount.get(interest) + "건 등록되었습니다.";
                List<Subscription> subscriptions = subscriptionRepository.findByInterestId(
                    interest.getId());
                for (Subscription sub : subscriptions) {
                    notifications.add(new Notification(sub.getUser(), content,
                        interest.getId(), ResourceType.INTEREST));
                }
            });
        notificationRepository.saveAll(notifications);
        return notifications;
    }

    @Transactional
    @Override
    public void readNotification(UUID userId, UUID notificationId) {
        // 사용자 정보 확인
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User Not Found");
        }
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification Not Found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("This user cannot access to this notification");
        }

        notification.confirm();
    }

    @Transactional
    @Override
    public void readAllNotifications(UUID userId) {
        // 사용자 정보 확인
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User Not Found");
        }
        notificationRepository.confirmAllByUserId(userId);
    }
}
