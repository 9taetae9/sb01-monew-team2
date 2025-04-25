package com.codeit.team2.monew.module.domain.user.controller;

import com.codeit.team2.monew.module.domain.user.controller.docs.UserControllerDocs;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<UserDto> registUser(@Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.registerUser(request));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
        @RequestHeader("Monew-Request-User-ID") UUID loginId,
        @PathVariable(name = "userId") UUID userId,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.updateUser(loginId, userId, request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request) {
        UserDto userDto = userService.login(request);
        return ResponseEntity.status(HttpStatus.OK)
            .header("Monew-Request-User-ID", userDto.id().toString())
            .body(userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> softDeleteUser(
        @RequestHeader("Monew-Request-User-ID") UUID loginId,
        @PathVariable(name = "userId") UUID userId
    ) {
        userService.softDeleteUser(loginId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build();
    }

    @DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> hardDeleteUser(
        @RequestHeader("Monew-Request-User-ID") UUID loginId,
        @PathVariable(name = "userId") UUID userId
    ) {
        userService.hardDeleteUser(loginId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build();
    }
}
