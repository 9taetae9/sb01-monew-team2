package com.codeit.team2.monew.module.domain.useractivity.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.exception.UserNotFoundException;
import com.codeit.team2.monew.module.domain.user.exception.UserUnauthorizedException;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import com.codeit.team2.monew.module.domain.useractivity.document.ArticleViewItem;
import com.codeit.team2.monew.module.domain.useractivity.document.CommentItem;
import com.codeit.team2.monew.module.domain.useractivity.document.CommentLikeItem;
import com.codeit.team2.monew.module.domain.useractivity.document.SubscriptionItem;
import com.codeit.team2.monew.module.domain.useractivity.document.UserActivity;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.mapper.UserActivityMapper;
import com.codeit.team2.monew.module.domain.useractivity.repository.MongoUserActivityRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.db-type", havingValue = "mongodb")
public class MongoUserActivityService implements UserActivityService {

    private final MongoUserActivityRepository userActivityRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final UserActivityMapper userActivityMapper;

    @Override
    public UserActivityDto findUserActivities(UUID loginId, UUID userId) {
        if (!loginId.equals(userId)) {
            log.debug("User Unauthorized: loginId={} userId={}", loginId, userId);
            throw new UserUnauthorizedException(loginId, userId);
        }

        UserActivity userActivity = findUserActivityOrSave(userId);

        return userActivityMapper.toUserActivityDto(userActivity);
    }

    public UserActivity createUserActivity(User user) {
        return userActivityRepository.save(
            new UserActivity(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
            )
        );
    }

    @Transactional
    public void createSubscriptionItem(Subscription subscription, Interest interest, UUID userId) {
        UserActivity userActivity = findUserActivityOrSave(userId);

        List<String> keywords = interest.getKeywords().stream().map(
            interestKeyword -> interestKeyword.getKeyword().getName()
        ).toList();

        SubscriptionItem subscriptionItem = new SubscriptionItem(
            subscription.getId(),
            interest.getId(),
            interest.getName(),
            keywords,
            interest.getSubscriberCount(),
            subscription.getCreatedAt()
        );

        userActivity.addSubscriptionItem(subscriptionItem);
        userActivityRepository.save(userActivity);
    }

    @Transactional
    public void createCommentItem(Comment comment, Article article, User user) {
        CommentItem commentItem = new CommentItem(
            comment.getId(),
            article.getId(),
            article.getTitle(),
            user.getId(),
            user.getNickname(),
            comment.getContent(),
            comment.getLikeCount(),
            comment.getCreatedAt()
        );

        UserActivity userActivity = findUserActivityOrSave(user.getId());

        userActivity.addCommentItem(commentItem);
        userActivityRepository.save(userActivity);
    }

    @Transactional
    public void createCommentLikeItem(CommentLike commentLike) {
        User user = commentLike.getUser();
        Comment comment = commentLike.getComment();
        Article article = comment.getArticle();

        UserActivity userActivity = findUserActivityOrSave(user.getId());

        CommentLikeItem commentLikeItem = new CommentLikeItem(
            commentLike.getId(),
            commentLike.getCreatedAt(),
            comment.getId(),
            article.getId(),
            article.getTitle(),
            user.getId(),
            user.getNickname(),
            comment.getContent(),
            comment.getLikeCount(),
            comment.getCreatedAt()
        );

        // CommentItem의 likeCount도 함께 갱신
        userActivity.getComments().stream()
            .filter(commentItem -> commentItem.getId().equals(comment.getId()))
            .findAny()
            .ifPresent(
                findedCommentItem -> findedCommentItem.updateLikeCount(comment.getLikeCount()));

        userActivity.addCommentLikeItem(commentLikeItem);
        userActivityRepository.save(userActivity);
    }

    public void createArticleViewItem(ArticleView articleView, User user) {
        UserActivity userActivity = findUserActivityOrSave(user.getId());
        Article article = articleView.getArticle();
        ArticleViewItem articleViewItem = new ArticleViewItem(
            articleView.getId(),
            user.getId(),
            articleView.getCreatedAt(),
            article.getId(),
            article.getSource(),
            article.getSourceUrl(),
            article.getTitle(),
            article.getPublishedDate(),
            article.getSummary(),
            commentRepository.countByArticle(article),
            article.getViewCount()
        );

        userActivity.addArticleViewItem(articleViewItem);
        userActivityRepository.save(userActivity);
    }

    public void updateUserNicknameInActivity(User user) {
        UserActivity userActivity = findUserActivityOrSave(user.getId());

        userActivity.updateNickname(user.getNickname());

        // CommentItem의 userNickname도 함께 갱신
        userActivity.getComments().stream()
            .filter(commentItem -> commentItem.getUserId().equals(user.getId()))
            .forEach(commentItem -> commentItem.updateUserNickname(user.getNickname()));

        // CommentLikeItem의 commentUserNickname도 함께 갱신
        userActivity.getCommentLikes().stream()
            .filter(commentLikeItem -> commentLikeItem.getCommentUserId().equals(user.getId()))
            .forEach(
                commentLikeItem -> commentLikeItem.updateCommentUserNickname(user.getNickname()));

        userActivityRepository.save(userActivity);
    }

    public void updateSubscriptionItemInActivity(Interest interest, List<String> keywords,
        UUID userId) {
        UserActivity userActivity = findUserActivityOrSave(userId);

        userActivity.getSubscriptions().stream()
            .filter(subscriptionItem -> subscriptionItem.getInterestId().equals(interest.getId()))
            .findAny()
            .ifPresent(subscriptionItem -> subscriptionItem.updateInterestKeywords(keywords));

        userActivityRepository.save(userActivity);
    }

    public void updateCommentContentInActivity(Comment comment, UUID userId) {
        UserActivity userActivity = findUserActivityOrSave(userId);

        // CommentItem의 content도 함께 갱신
        userActivity.getComments().stream()
            .filter(commentItem -> commentItem.getId().equals(comment.getId()))
            .forEach(commentItem -> commentItem.updateContent(comment.getContent()));

        // CommentLikeItem의 commentContent도 함께 갱신
        userActivity.getCommentLikes().stream()
            .filter(commentLikeItem -> commentLikeItem.getCommentId().equals(comment.getId()))
            .forEach(
                commentLikeItem -> commentLikeItem.updateCommentContent(comment.getContent()));

        userActivityRepository.save(userActivity);
    }

    @Transactional
    public void deleteSubscriptionItem(Subscription subscription, UUID userId) {
        UserActivity userActivity = findUserActivityOrSave(userId);

        userActivity.getSubscriptions()
            .removeIf(
                subscriptionItem -> subscriptionItem.getId().equals(subscription.getId()));

        userActivityRepository.save(userActivity);
    }

    @Transactional
    public void deleteSubscriptionItem(Interest interest, UUID userId) {
        UserActivity userActivity = findUserActivityOrSave(userId);

        userActivity.getSubscriptions()
            .removeIf(
                subscriptionItem -> subscriptionItem.getInterestId().equals(interest.getId()));

        userActivityRepository.save(userActivity);
    }

    public void deleteCommentLikeItem(CommentLike commentLike) {
        User user = commentLike.getUser();
        Comment comment = commentLike.getComment();

        UserActivity userActivity = findUserActivityOrSave(user.getId());

        // commentLikes에서 삭제
        userActivity.getCommentLikes()
            .removeIf(commentLikeItem -> commentLikeItem.getId().equals(commentLike.getId()));

        // CommentItem의 likeCount도 함께 갱신
        userActivity.getComments().stream()
            .filter(commentItem -> commentItem.getId().equals(comment.getId()))
            .findAny()
            .ifPresent(
                findedCommentItem -> findedCommentItem.updateLikeCount(comment.getLikeCount()));

        userActivityRepository.save(userActivity);
    }

    private UserActivity findUserActivityOrSave(UUID userId) {
        return userActivityRepository.findById(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.debug("User Not Found: id={}", userId);
                        return new UserNotFoundException(userId);
                    });
                return createUserActivity(user);
            });
    }
}
