package com.codeit.team2.monew.module.domain.article.dto;

import java.time.Instant;

public record FetchArticleDto(
    String title,
    String source,
    String link,
    String summary,
    Instant publishedDate
) {

}
