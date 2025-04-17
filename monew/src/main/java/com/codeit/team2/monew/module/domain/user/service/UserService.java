package com.codeit.team2.monew.module.domain.user.service;

import com.codeit.team2.monew.module.domain.user.dto.UserDto;
import com.codeit.team2.monew.module.domain.user.dto.UserRegisterRequest;

public interface UserService {

    UserDto registUser(UserRegisterRequest userRegisterRequest);

}
