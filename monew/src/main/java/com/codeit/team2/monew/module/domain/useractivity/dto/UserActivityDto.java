package com.codeit.team2.monew.module.domain.useractivity.dto;

import java.time.Instant;
import java.util.UUID;

public record UserActivityDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt,
    SubscriptionItems subscriptions,
    CommentItems comments,
    CommentLikeItems commentLikes,
    ArticleViewItems articleViews
) {

}
