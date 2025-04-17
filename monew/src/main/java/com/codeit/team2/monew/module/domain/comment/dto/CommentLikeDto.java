package com.codeit.team2.monew.module.domain.comment.dto;

import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
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

    public static CommentLikeDto from(CommentLike commentLike) {
        return new CommentLikeDto(
            commentLike.getId(),
            commentLike.getUser().getId(),
            commentLike.getCreatedAt(),
            commentLike.getComment().getId(),
            commentLike.getComment().getArticle().getId(),
            commentLike.getComment().getUser().getId(),
            commentLike.getComment().getUser().getNickname(),
            commentLike.getComment().getContent(),
            commentLike.getComment().getLikeCount(),
            commentLike.getComment().getCreatedAt()
        );
    }
}
