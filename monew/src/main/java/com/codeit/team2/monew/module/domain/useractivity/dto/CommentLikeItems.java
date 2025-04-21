package com.codeit.team2.monew.module.domain.useractivity.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentLikeItems(
    UUID id,
    Instant createdAt,
    UUID commentId,
    UUID articleId,
    String articleTitle,
    UUID commentUserId,
    String commentUserNickname,
    String commentContent,
    Integer commentLikeCount,
    Instant commentCreatedAt
) {

}
