package com.codeit.team2.monew.module.domain.article.dto;

public record FetchCommand(
    String keyword,
    int pageSize,
    int offset,
    String sortBy
) {
}
