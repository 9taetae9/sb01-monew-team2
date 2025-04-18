package com.codeit.team2.monew.module.domain.user.service;

import com.codeit.team2.monew.module.domain.user.dto.UserDto;
import com.codeit.team2.monew.module.domain.user.dto.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.dto.UserUpdateRequest;
import java.util.UUID;

public interface UserService {

    UserDto registUser(UserRegisterRequest userRegisterRequest);

    UserDto updateUser(UUID userId, UserUpdateRequest userUpdateRequest);
}
