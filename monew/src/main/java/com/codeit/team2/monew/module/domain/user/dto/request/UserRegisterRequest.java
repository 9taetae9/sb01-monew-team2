package com.codeit.team2.monew.module.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterRequest(

    @Email
    @NotBlank
    String email,

    @NotBlank
    String nickname,

    @NotBlank
    String password
) {

}
