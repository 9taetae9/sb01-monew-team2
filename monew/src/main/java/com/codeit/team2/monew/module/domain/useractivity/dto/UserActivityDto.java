package com.codeit.team2.monew.module.domain.useractivity.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserActivityDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt,
    List<SubscriptionItem> subscriptions,
    List<CommentItem> comments,
    List<CommentLikeItem> commentLikes,
    List<ArticleViewItem> articleViews
) {

}
