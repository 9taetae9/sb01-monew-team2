package com.codeit.team2.monew.module.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "수정할 댓글 정보")
public record CommentUpdateRequest(

    @Schema(description = "내용", maxLength = 500, minLength = 1)
    @NotBlank(message = "댓글 내용은 필수입니다.")
    String content
) {

}
