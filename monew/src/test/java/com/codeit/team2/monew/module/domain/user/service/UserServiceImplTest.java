package com.codeit.team2.monew.module.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.user.dto.request.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserUpdateRequest;
import com.codeit.team2.monew.module.domain.user.dto.response.UserDto;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.mapper.UserMapper;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    class registUserTest {

        @Test
        void 유저_등록_성공() {
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

        @Test
        void 이메일_중복_예외() {
            // given
            String email = "email";
            String nickname = "nickname";
            String password = "password";

            UserRegisterRequest request =
                new UserRegisterRequest(email, nickname, password);

            when(userRepository.existsByEmail(any())).thenReturn(true);

            // when & then
            assertThrows(Exception.class, () -> {
                userService.registUser(request);
            });
        }

        @Test
        void 닉네임_중복_예외() {
            // given
            String email = "email";
            String nickname = "nickname";
            String password = "password";

            UserRegisterRequest request =
                new UserRegisterRequest(email, nickname, password);

            when(userRepository.existsByNickname(any())).thenReturn(true);

            // when & then
            assertThrows(Exception.class, () -> {
                userService.registUser(request);
            });
        }
    }

    @Nested
    class updateUserTest {

        @Test
        void 유저_수정_성공() {
            // given
            UUID userId = UUID.randomUUID();
            UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newNickname");

            String email = "email";
            String nickname = "nickname";
            String password = "password";

            User user = new User(email, nickname, password, false);

            when(userRepository.findById(any())).thenReturn(Optional.of(user));

            // when
            UserDto userDto = userService.updateUser(userId, userUpdateRequest);

            // then
            assertEquals("newNickname", userDto.nickname());
        }
    }

    @Nested
    class loginTest {

        @Test
        void 로그인_성공() {
            // given
            String email = "a@a.com";
            String password = "password";
            String nickname = "nickname";

            UserLoginRequest userLoginRequest = new UserLoginRequest(email, password);

            User user = new User(email, nickname, password, false);
            when(userRepository.findByEmailAndPassword(any(), any())).thenReturn(Optional.of(user));

            // when
            UserDto userDto = userService.login(userLoginRequest);

            // then
            assertEquals(email, userDto.email());
            assertEquals(nickname, userDto.nickname());
        }
    }

}
