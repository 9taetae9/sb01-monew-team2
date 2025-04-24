package com.codeit.team2.monew.module.domain.notification.service;

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
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Notification createCommentNotification(Comment comment, User author, User liker) {
        if (!commentRepository.existsById(comment.getId())) {
            log.debug("[Notification Creation] Failed: Comment not found - commentId: {}",
                comment.getId());
            throw new RuntimeException("Comment not found");
        }
        if (!userRepository.existsById(author.getId())) {
            log.debug("[Notification Creation] Failed: Author not found - userId: {}",
                author.getId());
            throw new RuntimeException("Author not found");
        } else if (!userRepository.existsById(liker.getId())) {
            log.debug("[Notification Creation] Failed: Liker not found - userId: {}",
                liker.getId());
            throw new RuntimeException("Liker not found");
        }
        String content = liker.getNickname() + "님이 나의 댓글을 좋아합니다.";
        Notification notification = new Notification(author, content, comment.getId(),
            ResourceType.COMMENT);
        notificationRepository.save(notification);
        return notification;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<Notification> createArticleInterestNotification(
        List<ArticleInterest> articleInterests) {
        Map<Interest, List<Article>> interestToArticles = new HashMap<>();

        for (ArticleInterest ai : articleInterests) {
            Interest interest = ai.getInterest();
            interestToArticles.computeIfAbsent(interest, k -> new ArrayList<>())
                .add(ai.getArticle());
        }

        List<Notification> notifications = new ArrayList<>();

        for (Map.Entry<Interest, List<Article>> entry : interestToArticles.entrySet()) {
            Interest interest = entry.getKey();
            List<Article> articles = entry.getValue();

            String content = interest.getName() + "와 관련된 기사가 " + articles.size() + "건 등록되었습니다.";

            List<Subscription> subscriptions = subscriptionRepository.findAllByInterest(interest);

            for (Subscription sub : subscriptions) {
                notifications.add(new Notification(
                    sub.getUser(), content,
                    interest.getId(), ResourceType.INTEREST
                ));
            }
        }

        notificationRepository.saveAll(notifications);
        return notifications;
    }

    @Transactional
    @Override
    public void confirmNotification(UUID userId, UUID notificationId) {
        // 사용자 정보 확인
        if (!userRepository.existsById(userId)) {
            log.debug("[Notification Confirm] Failed: User not found - userId: {}", userId);
            throw new RuntimeException("User Not Found");
        }
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> {
                log.debug(
                    "[Notification Confirm] Failed: Notification not found - notificationId: {}",
                    notificationId);
                return new RuntimeException("Notification Not Found");
            });

        if (!notification.getUser().getId().equals(userId)) {
            log.debug(
                "[Notification Confirm] Failed: User Access Denied - userId: {}, notificationId = {}",
                userId, notificationId);
            throw new RuntimeException("User Access Denied");
        }

        notification.confirm();
    }

    @Transactional
    @Override
    public void confirmAllNotifications(UUID userId) {
        // 사용자 정보 확인
        if (!userRepository.existsById(userId)) {
            log.debug("[Notification Confirm] Failed: User not found - userId: {}", userId);
            throw new RuntimeException("User Not Found");
        }
        notificationRepository.confirmAllByUserId(userId);
    }

    @Override
    public CursorPageResponseNotificationDto findAll(UUID userId, Instant cursor, Instant after,
        int limit) {
        if (!userRepository.existsById(userId)) {
            log.debug("[Notification finding] Failed: User not found - userId: {}", userId);
            throw new RuntimeException("User Not Found");
        }
        // 정렬 조건은 시간 순으로 고정
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Direction.ASC, "createdAt"));
        Page<Notification> pages;
        if (cursor != null) {
            pages = notificationRepository.findPageWithCursor(userId, cursor, pageable);
        } else {
            pages = notificationRepository.findFirstPage(userId, pageable);
        }
        // dto로 변환
        List<Notification> notifications = pages.getContent();
        List<NotificationDto> notificationDtos = notifications.stream()
            .map(notification -> notificationMapper.toDto(notification))
            .collect(Collectors.toList());

        int size = pages.getSize();
        boolean hasNext = pages.hasNext();
        Instant nextCursor = null;
        Instant nextAfter = null;
        if (hasNext) {
            nextCursor = notificationDtos.get(notificationDtos.size() - 1).createdAt();
            nextAfter = nextCursor;
        }
        long totalElements = notificationRepository.countForPagination(userId);

        return new CursorPageResponseNotificationDto(notificationDtos, nextCursor, nextAfter, size,
            totalElements, hasNext);
    }
}
