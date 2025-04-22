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
    public UserDto registerUser(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByEmail(userRegisterRequest.email())) {
            throw new RuntimeException("duplicate email");
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
    public UserDto updateUser(UUID loginId, UUID userId, UserUpdateRequest userUpdateRequest) {
        validateAuthority(loginId, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("not found user"));

        user.updateNickname(userUpdateRequest.nickname());

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmailAndPasswordAndDeletedFalse(
            userLoginRequest.email(), userLoginRequest.password()
        ).orElseThrow(() -> new RuntimeException("not found user"));

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void softDeleteUser(UUID loginId, UUID userId) {
        validateAuthority(loginId, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("not found user"));

        user.updateDeleted(true);
    }

    @Override
    @Transactional
    public void hardDeleteUser(UUID loginId, UUID userId) {
        validateAuthority(loginId, userId);

        userRepository.deleteById(userId);
    }

    private void validateAuthority(UUID loginId, UUID userId) {
        if (!loginId.equals(userId)) {
            throw new RuntimeException("Not Authorized");
        }
    }
}
