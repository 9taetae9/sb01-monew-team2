package com.codeit.team2.monew.module.domain.comment.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentLikeDto(
    UUID id,
    UUID likedBy,
    Instant createdAt,
    UUID commentId,
    UUID articleId,
    UUID commentUserId,
    String commentUserNickname,
    String commentContent,
    long commentLikeCount,
    Instant commentCreatedAt
) {

}
