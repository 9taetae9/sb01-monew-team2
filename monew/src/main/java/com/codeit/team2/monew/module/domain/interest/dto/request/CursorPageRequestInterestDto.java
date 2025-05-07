package com.codeit.team2.monew.module.domain.interest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import org.springframework.data.domain.Sort.Direction;

@Schema(description = "관심사 목록 조회를 위한 커서 기반 페이지 요청")
public record CursorPageRequestInterestDto(

    @Schema(description = "검색어 (관심사 이름, 키워드)", example = "스포츠")
    String keyword,

    @Schema(description = "정렬 속성 이름", example = "name", allowableValues = {"name",
        "subscriberCount"})
    @NotNull
    InterestOrderBy orderBy,

    @Schema(description = "정렬 방향 (ASC, DESC)", example = "ASC", allowableValues = {"ASC", "DESC"})
    @NotNull
    Direction direction,

    @Schema(description = "커서 값")
    String cursor,

    @Schema(description = "보조 커서(createdAt) 값", format = "date-time")
    Instant after,

    @Schema(description = "커서 페이지 크기", example = "50")
    @NotNull
    int limit
) {

}
