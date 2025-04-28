package com.codeit.team2.monew.module.domain.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "뉴스 기사 정보")
public record ArticleDto(

    @Schema(description = "기사 ID", format = "uuid")
    UUID id,

    @Schema(description = "출처", example = "NAVER", allowableValues = {"NAVER"})
    String source,

    @Schema(description = "원본 기사 URL")
    String sourceUrl,

    @Schema(description = "제목")
    String title,

    @Schema(description = "날짜", format = "date-time")
    Instant publishDate,

    @Schema(description = "요약")
    String summary,

    @Schema(description = "댓글 수", format = "int64")
    Long commentCount,

    @Schema(description = "조회 수", format = "int64")
    Long viewCount,

    @Schema(description = "요청자의 조회 여부")
    Boolean viewedByMe
) {

}
