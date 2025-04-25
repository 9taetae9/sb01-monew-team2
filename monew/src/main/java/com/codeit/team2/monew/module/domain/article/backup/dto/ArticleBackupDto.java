package com.codeit.team2.monew.module.domain.article.backup.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * 백업용 Article DTO
 * AWS S3에 json 형태로 저장될 객체
 */
public record ArticleBackupDto(
    UUID id,
    String title,
    String source,
    String sourceUrl,
    String summary,
    Long viewCount,
    Instant publishedDate,
    Boolean deleted,
    Set<UUID> interestIds,
    Instant backupDate
) {
}
