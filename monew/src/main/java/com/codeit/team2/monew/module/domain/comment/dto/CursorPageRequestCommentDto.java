package com.codeit.team2.monew.module.domain.comment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Sort;

public record CursorPageRequestCommentDto(
    UUID articleId,
    @NotNull
    CommentOrderBy orderBy,
    @NotNull
    Sort.Direction direction,
    Object cursor,
    Instant after,
    @NotNull
    int limit
) {

}
