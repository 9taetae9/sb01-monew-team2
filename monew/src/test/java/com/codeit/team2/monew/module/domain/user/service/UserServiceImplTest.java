package com.codeit.team2.monew.module.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.user.dto.UserDto;
import com.codeit.team2.monew.module.domain.user.dto.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registUserTest() {
        // given
        String email = "email";
        String nickname = "nickname";
        String password = "password";

        UserRegisterRequest request =
            new UserRegisterRequest(email, nickname, password);

        User user = new User(email, nickname, password, false);
        when(userRepository.save(any())).thenReturn(user);

        // when
        UserDto userDto = userService.registUser(request);

        // then
        assertEquals("email", userDto.email());
    }
}