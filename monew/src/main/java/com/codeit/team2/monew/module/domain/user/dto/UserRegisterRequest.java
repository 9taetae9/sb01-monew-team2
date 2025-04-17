package com.codeit.team2.monew.module.domain.user.dto;

public record UserRegisterRequest(
    String email,
    String nickname,
    String password
) {

}
