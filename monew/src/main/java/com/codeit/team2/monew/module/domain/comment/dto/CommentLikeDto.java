package com.codeit.team2.monew.module.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "댓글 좋아요 DTO")
public record CommentLikeDto(

    @Schema(description = "좋아요 ID", format = "uuid")
    UUID id,

    @Schema(description = "좋아요한 사용자 ID", format = "uuid")
    UUID likedBy,

    @Schema(description = "좋아요한 날짜", format = "date-time")
    Instant createdAt,

    @Schema(description = "댓글 ID", format = "uuid")
    UUID commentId,

    @Schema(description = "기사 ID", format = "uuid")
    UUID articleId,

    @Schema(description = "작성자 ID", format = "uuid")
    UUID commentUserId,

    @Schema(description = "작성자 닉네임")
    String commentUserNickname,

    @Schema(description = "내용")
    String commentContent,

    @Schema(description = "좋아요 수", format = "int64")
    long commentLikeCount,

    @Schema(description = "작성된 날짜", format = "date-time")
    Instant commentCreatedAt
) {

}
