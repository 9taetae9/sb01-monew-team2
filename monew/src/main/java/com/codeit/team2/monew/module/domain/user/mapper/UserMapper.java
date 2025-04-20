package com.codeit.team2.monew.module.domain.user.mapper;

import com.codeit.team2.monew.module.domain.user.dto.request.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.dto.response.UserDto;
import com.codeit.team2.monew.module.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "deleted", constant = "false")
    User toUser(UserRegisterRequest userRegisterRequest);

    UserDto toUserDto(User user);
}
