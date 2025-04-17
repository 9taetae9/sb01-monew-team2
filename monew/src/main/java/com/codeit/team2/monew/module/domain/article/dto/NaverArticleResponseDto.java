package com.codeit.team2.monew.module.domain.article.dto;

import java.util.List;

public record NaverArticleResponseDto(
    String lastBuildDate,
    int total,
    int start,
    int display,
    List<NaverArticleItemDto> items
) {
}
