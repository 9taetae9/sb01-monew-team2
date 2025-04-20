package com.codeit.team2.monew.module.domain.user.controller;

import com.codeit.team2.monew.module.domain.user.dto.request.UserLoginRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserUpdateRequest;
import com.codeit.team2.monew.module.domain.user.dto.response.UserDto;
import com.codeit.team2.monew.module.domain.user.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<UserDto> registUser(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.registUser(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
        @RequestHeader("MoNew-Request-User-ID") UUID loginId,
        @PathVariable(name = "id") UUID userId,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.updateUser(loginId, userId, request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request) {
        UserDto userDto = userService.login(request);
        return ResponseEntity.status(HttpStatus.OK)
            .header("MoNew-Request-User-ID", userDto.id().toString())
            .body(userDto);
    }
}
