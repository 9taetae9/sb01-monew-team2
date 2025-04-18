package com.codeit.team2.monew.module.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(

    @NotBlank
    String nickname
) {

}
