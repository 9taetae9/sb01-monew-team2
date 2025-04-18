package com.codeit.team2.monew.module.domain.user.service;

import com.codeit.team2.monew.module.domain.user.dto.request.UserLoginRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserUpdateRequest;
import com.codeit.team2.monew.module.domain.user.dto.response.UserDto;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.mapper.UserMapper;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // password 암호화는 추후 진행

        User user = userRepository.save(userMapper.toUser(userRegisterRequest));

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID userId, UserUpdateRequest userUpdateRequest) {
        // 사용자 본인의 userId로만 수정 가능하도록 (추후 추가)

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("not found user"));

        user.updateNickname(userUpdateRequest.nickname());

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmailAndPassword(
            userLoginRequest.email(), userLoginRequest.password()
        ).orElseThrow(() -> new RuntimeException("not found user"));

        return userMapper.toUserDto(user);
    }
}
