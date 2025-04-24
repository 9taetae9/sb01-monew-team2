package com.codeit.team2.monew.module.domain.article.dto;

import java.time.Instant;
import java.util.UUID;

public record ArticleDto(
    UUID id,
    String source,
    String sourceUrl,
    String title,
    Instant publishDate,
    String summary,
    Long commentCount,
    Long viewCount,
    Boolean viewedByMe
) {

}
