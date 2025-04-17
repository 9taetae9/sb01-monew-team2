package com.codeit.team2.monew.module.domain.user.service;

import com.codeit.team2.monew.module.domain.user.dto.UserDto;
import com.codeit.team2.monew.module.domain.user.dto.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto registUser(UserRegisterRequest userRegisterRequest) {
        User user = userRepository.save(
            new User(
                userRegisterRequest.email(),
                userRegisterRequest.nickname(),
                userRegisterRequest.password(),
                false
            )
        );

        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getCreatedAt()
        );
    }
}
