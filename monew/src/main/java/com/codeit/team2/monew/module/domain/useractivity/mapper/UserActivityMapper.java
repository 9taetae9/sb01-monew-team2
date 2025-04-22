package com.codeit.team2.monew.module.domain.useractivity.mapper;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.useractivity.dto.ArticleViewItem;
import com.codeit.team2.monew.module.domain.useractivity.dto.CommentItem;
import com.codeit.team2.monew.module.domain.useractivity.dto.CommentLikeItem;
import com.codeit.team2.monew.module.domain.useractivity.dto.SubscriptionItem;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserActivityMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "interest.id", target = "interestId")
    @Mapping(source = "interest.name", target = "interestName")
    @Mapping(expression = "java((long) subscription.getInterest().getSubscriberCount())",
        target = "interestSubscriberCount")
    @Mapping(source = "interest.keywords", target = "interestKeywords")
    SubscriptionItem toSubscriptionItem(Subscription subscription);

    default List<String> mapInterestKeywords(List<InterestKeyword> keywords) {
        if (keywords == null) {
            return Collections.emptyList();
        }
        return keywords.stream()
            .map(k -> k.getKeyword().getName())
            .collect(Collectors.toList());
    }

    @Mapping(source = "comment.id", target = "id")
    @Mapping(source = "comment.article.id", target = "articleId")
    @Mapping(source = "comment.article.title", target = "articleTitle")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.nickname", target = "userNickname")
    @Mapping(source = "comment.content", target = "content")
    @Mapping(source = "comment.likeCount", target = "likeCount")
    @Mapping(source = "comment.createdAt", target = "createdAt")
    CommentItem toCommentItem(User user, Comment comment);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "comment.article.id", target = "articleId")
    @Mapping(source = "comment.article.title", target = "articleTitle")
    @Mapping(source = "comment.user.id", target = "commentUserId")
    @Mapping(source = "comment.user.nickname", target = "commentUserNickname")
    @Mapping(source = "comment.content", target = "commentContent")
    @Mapping(source = "comment.likeCount", target = "commentLikeCount")
    @Mapping(source = "comment.createdAt", target = "commentCreatedAt")
    CommentLikeItem toCommentLikeItem(CommentLike commentLike);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "user.createdAt", target = "createdAt")
    @Mapping(source = "subscriptionItems", target = "subscriptions")
    @Mapping(source = "commentItems", target = "comments")
    @Mapping(source = "commentLikeItems", target = "commentLikes")
    @Mapping(source = "articleViewItems", target = "articleViews")
    UserActivityDto toUserActivityDto(
        User user,
        List<SubscriptionItem> subscriptionItems,
        List<CommentItem> commentItems,
        List<CommentLikeItem> commentLikeItems,
        List<ArticleViewItem> articleViewItems
    );
}
