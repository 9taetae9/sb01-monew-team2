package com.codeit.team2.monew.module.domain.article.dto;

public record FetchArticleDto(
    String title,
    String source,
    String link,
    String summary,
    String publishedDate // TODO : data type
) {

}
