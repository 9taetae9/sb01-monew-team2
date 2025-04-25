package com.codeit.team2.monew.module.domain.useractivity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "최근 본 기사 정보")
public record ArticleViewItemDto(

    @Schema(description = "기사 조회 ID", format = "uuid")
    UUID id,

    @Schema(description = "기사를 조회한 사용자 ID", format = "uuid")
    UUID viewedBy,

    @Schema(description = "기사를 본 날짜", format = "date-time")
    Instant createdAt,

    @Schema(description = "기사 ID", format = "uuid")
    UUID articleId,

    @Schema(description = "출처", example = "NAVER", allowableValues = {"NAVER"})
    String source,

    @Schema(description = "원본 기사 URL")
    String sourceUrl,

    @Schema(description = "제목")
    String articleTitle,

    @Schema(description = "날짜", format = "date-time")
    Instant articlePublishedDate,

    @Schema(description = "요약")
    String articleSummary,

    @Schema(description = "댓글 수", format = "int64")
    Long articleCommentCount,

    @Schema(description = "조회 수", format = "int64")
    Long articleViewCount

) {

}
