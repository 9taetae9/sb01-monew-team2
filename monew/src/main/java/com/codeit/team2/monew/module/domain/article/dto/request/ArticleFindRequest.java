package com.codeit.team2.monew.module.domain.article.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort.Direction;

public record ArticleFindRequest(
    @Nullable
    String keyword,
    @Nullable
    UUID interestId,
    @Nullable
    List<ArticleSourceIn> sourceIn,
    @Nullable
    Instant publishDateFrom,
    @Nullable
    Instant publishDateTo,
    @NotNull(message = "정렬 속성을 지정하세요.")
    ArticleOrderBy orderBy,
    @NotNull(message = "정렬 방향을 지정하세요.")
    Direction direction,
    @Nullable
    String cursor,
    @Nullable
    Instant after,
    @NotNull(message = "커서 페이지 크기를 지정하세요.")
    int limit

) {

}
