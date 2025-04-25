package com.codeit.team2.monew.module.domain.interest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "관심사 조회 커서 기반 페이지 응답")
public record CursorPageResponseInterestDto(

    @Schema(description = "페이지 내용")
    List<InterestDto> content,

    @Schema(description = "다음 페이지 커서")
    Object nextCursor,

    @Schema(description = "다음 보조 커서(마지막 요소의 생성 시간)", format = "date-time", example = "2025-04-06T15:04:05.000Z")
    Instant nextAfter,

    @Schema(description = "페이지 크기", example = "10")
    int size,

    @Schema(description = "총 요소 수", example = "100")
    long totalElements,

    @Schema(description = "다음 페이지 여부", example = "true")
    boolean hasNext

) {}
