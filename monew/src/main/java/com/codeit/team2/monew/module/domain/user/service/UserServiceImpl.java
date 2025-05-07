package com.codeit.team2.monew.module.domain.user.service;

import com.codeit.team2.monew.module.domain.user.dto.request.UserLoginRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserUpdateRequest;
import com.codeit.team2.monew.module.domain.user.dto.response.UserDto;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.event.UserRegisterEvent;
import com.codeit.team2.monew.module.domain.user.event.UserUpdateEvent;
import com.codeit.team2.monew.module.domain.user.exception.UserEmailAlreadyExistsException;
import com.codeit.team2.monew.module.domain.user.exception.UserNicknameAlreadyExistsException;
import com.codeit.team2.monew.module.domain.user.exception.UserNotFoundException;
import com.codeit.team2.monew.module.domain.user.mapper.UserMapper;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public UserDto registerUser(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByEmail(userRegisterRequest.email())) {
            throw new UserEmailAlreadyExistsException(userRegisterRequest.email());
        }

        if (userRepository.existsByNickname(userRegisterRequest.nickname())) {
            throw new UserNicknameAlreadyExistsException(userRegisterRequest.nickname());
        }

        // password 암호화는 추후 진행

        User user = userRepository.save(userMapper.toUser(userRegisterRequest));

        // 사용자 생성 이벤트 발생
        publisher.publishEvent(new UserRegisterEvent(user));

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID loginId, UUID userId, UserUpdateRequest userUpdateRequest) {
        validateAuthority(loginId, userId);

        User user = findUserOrThrow(userId);

        user.updateNickname(userUpdateRequest.nickname());

        // 사용자 닉네임 수정 이벤트 발생
        publisher.publishEvent(new UserUpdateEvent(user));

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmailAndPasswordAndDeletedFalse(
            userLoginRequest.email(), userLoginRequest.password()
        ).orElseThrow(() -> new UserNotFoundException(userLoginRequest.email()));

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void softDeleteUser(UUID loginId, UUID userId) {
        validateAuthority(loginId, userId);

        User user = findUserOrThrow(userId);

        user.updateDeleted(true);
    }

    @Override
    @Transactional
    public void hardDeleteUser(UUID loginId, UUID userId) {
        validateAuthority(loginId, userId);

        findUserOrThrow(userId);

        userRepository.deleteById(userId);
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void validateAuthority(UUID loginId, UUID userId) {
        if (!loginId.equals(userId)) {
            throw new RuntimeException("Not Authorized");
        }
    }
}
