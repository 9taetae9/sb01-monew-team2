package com.codeit.team2.monew.module.domain.article.dto;


/**
 * naver news api 의 각 news item dto
 */
public record NaverArticleItemDto(
    String title,
    String originalLink,
    String link,
    String description,
    String pubDate
) {

}
