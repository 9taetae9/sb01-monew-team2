package com.codeit.team2.monew.module.domain.article.backup.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ArticleRestoreResultDto(
    Instant restoreDate,
    List<UUID> restoredArticleIds,
    Long restoredArticleCount
) {

}
