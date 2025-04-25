package com.codeit.team2.monew.module.domain.useractivity.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.useractivity.document.ArticleViewItem;
import com.codeit.team2.monew.module.domain.useractivity.document.CommentItem;
import com.codeit.team2.monew.module.domain.useractivity.document.SubscriptionItem;
import com.codeit.team2.monew.module.domain.useractivity.document.UserActivity;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.mapper.UserActivityMapper;
import com.codeit.team2.monew.module.domain.useractivity.repository.MongoUserActivityRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.db-type", havingValue = "mongodb")
public class MongoUserActivityService implements UserActivityService {

    private final MongoUserActivityRepository userActivityRepository;
    private final CommentRepository commentRepository;
    private final UserActivityMapper userActivityMapper;

    @Override
    public UserActivityDto findUserActivities(UUID loginId, UUID userId) {
        if (!loginId.equals(userId)) {
            throw new RuntimeException("Not Authorized");
        }

        UserActivity userActivity = userActivityRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Not Found UserActivity"));

        return userActivityMapper.toUserActivityDto(userActivity);
    }

    public void createUserActivity(User user) {
        userActivityRepository.save(
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
        UserActivity userActivity = findUserActivityOrThrow(userId);

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

        UserActivity userActivity = findUserActivityOrThrow(user.getId());

        userActivity.addCommentItem(commentItem);
        userActivityRepository.save(userActivity);
    }

    public void createArticleViewItem(ArticleView articleView, User user) {
        UserActivity userActivity = findUserActivityOrThrow(user.getId());
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

    private UserActivity findUserActivityOrThrow(UUID userId) {
        return userActivityRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Not Found UserActivity"));
    }
}
