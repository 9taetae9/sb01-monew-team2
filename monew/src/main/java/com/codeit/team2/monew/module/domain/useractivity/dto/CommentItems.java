package com.codeit.team2.monew.module.domain.useractivity.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentItems(
    UUID id,
    UUID articleId,
    String articleTitle,
    UUID userId,
    String userNickname,
    String content,
    Integer likeCount,
    Instant createdAt
) {

}
