package com.codeit.team2.monew.module.domain.article.dto;

import java.time.Instant;
import java.util.UUID;

public record ArticleViewDto(
    UUID id,
    UUID viewedBy,
    Instant createdAt,
    UUID articleId,
    String source,
    String sourceUrl,
    String articleTitle,
    Instant articlePublishedDate,
    String articleSummary,
    Integer articleCommentCount,
    Integer articleViewCount
) {

}
