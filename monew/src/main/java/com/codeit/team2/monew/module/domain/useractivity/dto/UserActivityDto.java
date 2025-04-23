package com.codeit.team2.monew.module.domain.useractivity.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserActivityDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt,
    List<SubscriptionItemDto> subscriptions,
    List<CommentItemDto> comments,
    List<CommentLikeItemDto> commentLikes,
    List<ArticleViewItemDto> articleViews
) {

}
