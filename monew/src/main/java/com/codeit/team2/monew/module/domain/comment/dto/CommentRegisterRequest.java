package com.codeit.team2.monew.module.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "댓글 등록 요청 정보")
public record CommentRegisterRequest(

    @Schema(description = "기사 ID", format = "uuid")
    @NotNull(message = "기사 ID는 필수입니다.")
    UUID articleId,

    @Schema(description = "요청 사용자 ID", format = "uuid")
    @NotNull(message = "사용자 ID는 필수입니다.")
    UUID userId,

    @Schema(description = "내용", maxLength = 500, minLength = 1)
    @NotBlank(message = "댓글 내용은 필수입니다.")
    String content
) {

}
