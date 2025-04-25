package com.codeit.team2.monew.module.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "회원가입 정보")
public record UserRegisterRequest(

    @Schema(description = "가입 이메일")
    @Email
    @NotBlank
    String email,

    @Schema(description = "가입 닉네임", minLength = 1, maxLength = 20)
    @NotBlank
    String nickname,

    @Schema(description = "가입 비밀번호", minLength = 6, maxLength = 20)
    @NotBlank
    String password

) {

}
