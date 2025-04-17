package com.codeit.team2.monew.module.domain.user.service;

import com.codeit.team2.monew.module.domain.user.dto.UserDto;
import com.codeit.team2.monew.module.domain.user.dto.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.mapper.UserMapper;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto registUser(UserRegisterRequest userRegisterRequest) {

        if (userRepository.existsByEmail(userRegisterRequest.email())) {
            throw new RuntimeException("duplicate eamil");
        }

        if (userRepository.existsByNickname(userRegisterRequest.nickname())) {
            throw new RuntimeException("duplicate nickname");
        }

        User user = userRepository.save(userMapper.toUser(userRegisterRequest));

        return userMapper.toUserDto(user);
    }
}
