package com.codeit.team2.monew.module.domain.useractivity.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentItemDto(
    UUID id,
    UUID articleId,
    String articleTitle,
    UUID userId,
    String userNickname,
    String content,
    Long likeCount,
    Instant createdAt
) {

}
