package com.codeit.team2.monew.module.domain.notification.controller.docs;

import com.codeit.team2.monew.config.SwaggerTags.Descriptions;
import com.codeit.team2.monew.config.SwaggerTags.Tags;
import com.codeit.team2.monew.module.domain.notification.dto.CursorPageResponseNotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = Tags.NOTIFICATION, description = Descriptions.NOTIFICATION)
@RequestMapping("/api/notifications")
public interface NotificationControllerDocs {

    @Operation(
        summary = "알림 목록 조회",
        description = "요청자의 알림 목록을 조회합니다.",
        parameters = {
            @Parameter(name = "cursor", description = "커서 값"),
            @Parameter(name = "after", description = "보조 커서 (createdAt 값)"),
            @Parameter(name = "limit", description = "커서 페이지 크기", example = "50"),
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CursorPageResponseNotificationDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("")
    ResponseEntity<CursorPageResponseNotificationDto> findAll(UUID userId, Instant cursor, Instant after, int limit);

    @Operation(
        summary = "전체 알림 확인",
        description = "전체 알림을 한번에 확인합니다.",
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "전체 알림 확인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)"),
        @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PatchMapping("")
    ResponseEntity<Void> confirmAllNotifications(UUID userId);

    @Operation(
        summary = "알림 확인",
        description = "요청자가 특정 알림 하나를 확인 상태로 변경합니다.",
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER),
            @Parameter(name = "notificationId", description = "확인할 알림 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 확인 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)"),
        @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PatchMapping("/{notificationId}")
    ResponseEntity<Void> confirmNotification(UUID userId, UUID notificationId);
}
