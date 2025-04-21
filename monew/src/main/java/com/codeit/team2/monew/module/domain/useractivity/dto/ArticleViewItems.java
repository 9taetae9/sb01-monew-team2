package com.codeit.team2.monew.module.domain.useractivity.dto;

import java.time.Instant;
import java.util.UUID;

public record ArticleViewItems(
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
