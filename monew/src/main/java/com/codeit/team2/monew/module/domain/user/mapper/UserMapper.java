package com.codeit.team2.monew.module.domain.user.mapper;

import com.codeit.team2.monew.module.domain.user.dto.UserDto;
import com.codeit.team2.monew.module.domain.user.dto.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "deleted", ignore = true)
    User toUser(UserRegisterRequest userRegisterRequest);

    UserDto toUserDto(User user);
}
