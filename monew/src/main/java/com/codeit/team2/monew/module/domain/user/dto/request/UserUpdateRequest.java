package com.codeit.team2.monew.module.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "수정할 사용자 정보")
public record UserUpdateRequest(

    @Schema(description = "수정 닉네임", minLength = 1, maxLength = 20)
    @NotBlank
    String nickname

) {

}
