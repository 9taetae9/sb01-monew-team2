package com.codeit.team2.monew.module.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 정보")
public record UserLoginRequest(

    @Schema(description = "로그인 이메일")
    @Email
    @NotBlank
    String email,

    @Schema(description = "로그인 비밀번호")
    @NotBlank
    String password
) {

}
