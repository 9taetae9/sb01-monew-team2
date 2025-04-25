package com.codeit.team2.monew.module.domain.useractivity.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.useractivity.document.CommentItem;
import com.codeit.team2.monew.module.domain.useractivity.document.UserActivity;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.mapper.UserActivityMapper;
import com.codeit.team2.monew.module.domain.useractivity.repository.MongoUserActivityRepository;
import java.util.Collections;
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
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
            )
        );
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

        UserActivity userActivity = userActivityRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("Not Found UserActivity"));

        userActivity.addCommentItem(commentItem);
        userActivityRepository.save(userActivity);
    }
}
