package com.codeit.team2.monew.module.domain.article.dto;

import java.util.List;


/**
 * naver news api 전체 response dto
 */
public record NaverArticleResponseDto(
    String lastBuildDate,
    int total,
    int start,
    int display,
    List<NaverArticleItemDto> items
) {

}
