package com.codeit.team2.monew.module.domain.comment.dto;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import java.time.Instant;
import java.util.UUID;

public record CommentDto(
    UUID id,
    UUID articleId,
    UUID userId,
    String userNickname,
    String content,
    long likeCount,
    boolean likedByMe,
    Instant createdAt
) {

    public static CommentDto from(Comment comment, boolean likedByMe) {
        return new CommentDto(
            comment.getId(),
            comment.getArticle().getId(),
            comment.getUser().getId(),
            comment.getUser().getNickname(),
            comment.getContent(),
            comment.getLikeCount(),
            likedByMe,
            comment.getCreatedAt()
        );
    }

}
