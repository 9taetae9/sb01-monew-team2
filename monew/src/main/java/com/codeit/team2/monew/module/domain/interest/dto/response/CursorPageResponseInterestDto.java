package com.codeit.team2.monew.module.domain.interest.dto.response;

import java.time.Instant;
import java.util.List;

public record CursorPageResponseInterestDto(
    List<InterestDto> content,
    Object nextCursor,
    Instant nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
