package com.codeit.team2.monew.module.domain.article.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort.Direction;

public record CursorPageRequestArticleDto(

    @Schema(description = "검색어 (제목, 요약)")
    @Nullable
    String keyword,

    @Schema(description = "관심사 ID")
    @Nullable
    UUID interestId,

    @Schema(description = "기사 출처 필터 목록 (예: NAVER 등)")
    @Nullable
    List<ArticleSourceIn> sourceIn,

    @Schema(description = "검색 시작일")
    @Nullable
    String publishDateFrom,

    @Schema(description = "검색 종료일")
    @Nullable
    String publishDateTo,

    @Schema(description = "정렬 기준", required = true)
    @NotNull(message = "정렬 속성을 지정하세요.")
    ArticleOrderBy orderBy,

    @Schema(description = "정렬 방향 (ASC, DESC)", required = true)
    @NotNull(message = "정렬 방향을 지정하세요.")
    Direction direction,

    @Schema(description = "커서 값 (마지막 요소의 ID 등)")
    @Nullable
    String cursor,

    @Schema(description = "보조 커서 (마지막 요소의 생성 시간)")
    @Nullable
    Instant after,

    @Schema(description = "커서 페이지 크기", example = "50", required = true)
    @NotNull(message = "커서 페이지 크기를 지정하세요.")
    int limit

) {

    public Instant getPublishDateFromInstant() {
        return parseFromString(publishDateFrom);
    }

    public Instant getPublishDateToInstant() {
        return parseFromString(publishDateTo);
    }

    // String -> Instant형으로 변환
    private Instant parseFromString(String s) {
        if (s == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .withZone(
                ZoneId.of("Asia/Seoul"));
        return Instant.from(formatter.parse(s));
    }

}
