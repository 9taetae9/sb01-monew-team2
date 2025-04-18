package com.codeit.team2.monew.module.domain.user.service;

import com.codeit.team2.monew.module.domain.user.dto.request.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserUpdateRequest;
import com.codeit.team2.monew.module.domain.user.dto.response.UserDto;
import java.util.UUID;

public interface UserService {

    UserDto registUser(UserRegisterRequest userRegisterRequest);

    UserDto updateUser(UUID userId, UserUpdateRequest userUpdateRequest);
}
