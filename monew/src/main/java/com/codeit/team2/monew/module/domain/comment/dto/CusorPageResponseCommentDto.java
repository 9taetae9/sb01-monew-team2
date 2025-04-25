package com.codeit.team2.monew.module.domain.comment.dto;

import java.util.List;

public record CusorPageResponseCommentDto(
    List<CommentDto> content,
    Object nextCursor,
    Object nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {

}
