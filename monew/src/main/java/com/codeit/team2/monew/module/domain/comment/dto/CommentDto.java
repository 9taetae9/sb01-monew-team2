package com.codeit.team2.monew.module.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "댓글 정보")
public record CommentDto(

    @Schema(description = "댓글 ID", format = "uuid")
    UUID id,

    @Schema(description = "기사 ID", format = "uuid")
    UUID articleId,

    @Schema(description = "작성자 ID", format = "uuid")
    UUID userId,

    @Schema(description = "작성자 닉네임")
    String userNickname,

    @Schema(description = "내용")
    String content,

    @Schema(description = "좋아요 수", format = "int64")
    long likeCount,

    @Schema(description = "요청자의 좋아요 여부")
    boolean likedByMe,

    @Schema(description = "작성된 날짜", format = "date-time")
    Instant createdAt
) {

}
