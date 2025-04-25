package com.codeit.team2.monew.module.domain.comment.dto;

import java.util.List;

public record CursorPageResponseCommentDto(
    List<CommentDto> content,
    Object nextCursor,
    Object nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
