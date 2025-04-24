package com.codeit.team2.monew.module.domain.article.dto;

import java.time.Instant;
import java.util.List;

public record CursorPageResponseArticleDto(
    List<ArticleDto> content,
    Object nextCursor,  // publishDate, commentCount, viewCount
    Instant nextAfter,  // createdAt 기준
    int size,
    long totalElements,
    boolean hasNext
) {

}
