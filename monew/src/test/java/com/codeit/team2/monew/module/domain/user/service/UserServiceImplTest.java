package com.codeit.team2.monew.module.domain.user.service;

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
    void userCreate() {
        // given
        UserRegisterRequest request =
            new UserRegisterRequest("email", "nickname", "passowrd");

        // when
        UserDto userDto = UseruserService.createUser(request);

        // then
        assertEquals("email", userDto.email());
    }
}