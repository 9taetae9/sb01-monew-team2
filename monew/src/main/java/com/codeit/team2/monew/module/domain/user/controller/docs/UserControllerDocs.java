package com.codeit.team2.monew.module.domain.user.controller.docs;

import com.codeit.team2.monew.config.SwaggerTags.Descriptions;
import com.codeit.team2.monew.config.SwaggerTags.Tags;
import com.codeit.team2.monew.module.domain.user.dto.request.UserLoginRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserUpdateRequest;
import com.codeit.team2.monew.module.domain.user.dto.response.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = Tags.USER, description = Descriptions.USER)
@RequestMapping("/api/users")
public interface UserControllerDocs {

    @Operation(
        summary = "회원가입",
        description = "새로운 사용자를 등록합니다.",
        requestBody = @RequestBody(
            content = @Content(schema = @Schema(implementation = UserRegisterRequest.class))
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)"),
        @ApiResponse(responseCode = "409", description = "이메일 중복"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("")
    ResponseEntity<UserDto> registUser(UserRegisterRequest request);

    @Operation(
        summary = "사용자 정보 수정",
        description = "사용자의 닉네임을 수정합니다.",
        requestBody = @RequestBody(
            content = @Content(schema = @Schema(implementation = UserUpdateRequest.class))
        ),
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER),
            @Parameter(name = "userId", description = "수정할 사용자 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)"),
        @ApiResponse(responseCode = "403", description = "사용자 정보 수정 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PatchMapping("/{userId}")
    ResponseEntity<UserDto> updateUser(UUID loginId, UUID userId, UserUpdateRequest request);

    @Operation(
        summary = "로그인",
        description = "사용자 로그인을 처리합니다.",
        requestBody = @RequestBody(
            content = @Content(schema = @Schema(implementation = UserLoginRequest.class))
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)"),
        @ApiResponse(responseCode = "401", description = "로그인 실패 (이메일 또는 비밀번호 불일치)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/login")
    ResponseEntity<UserDto> login(UserLoginRequest request);

    @Operation(
        summary = "사용자 논리 삭제",
        description = "사용자를 논리적으로 삭제합니다.",
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER),
            @Parameter(name = "userId", description = "사용자 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "403", description = "사용자 삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{userId}")
    ResponseEntity<?> softDeleteUser(UUID loginId, UUID userId);

    @Operation(
        summary = "사용자 물리 삭제",
        description = "사용자를 물리적으로 삭제합니다.",
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER),
            @Parameter(name = "userId", description = "사용자 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "403", description = "사용자 삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{userId}/hard")
    ResponseEntity<?> hardDeleteUser(UUID loginId, UUID userId);
}
