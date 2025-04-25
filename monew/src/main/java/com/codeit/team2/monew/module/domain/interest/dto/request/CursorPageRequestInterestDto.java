package com.codeit.team2.monew.module.domain.interest.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import org.springframework.data.domain.Sort.Direction;

public record CursorPageRequestInterestDto(
    String keyword,
    @NotNull
    InterestOrderBy orderBy,
    @NotNull
    Direction direction,
    Object cursor,
    Instant after,
    @NotNull
    int limit
) {

}
