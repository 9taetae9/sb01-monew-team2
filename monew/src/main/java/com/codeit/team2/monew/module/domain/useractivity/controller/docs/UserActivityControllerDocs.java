package com.codeit.team2.monew.module.domain.useractivity.controller.docs;

import com.codeit.team2.monew.config.SwaggerTags.Descriptions;
import com.codeit.team2.monew.config.SwaggerTags.Tags;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = Tags.USER_ACTIVITY, description = Descriptions.USER_ACTIVITY)
@RequestMapping("/api/user-activities")
public interface UserActivityControllerDocs {

    @Operation(
        summary = "사용자 활동 내역 조회",
        description = "사용자 ID로 활동 이력을 조회합니다.",
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER),
            @Parameter(name = "userId", description = "사용자 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 활동 내역 조회 성공",
            content = @Content(schema = @Schema(implementation = UserActivityDto.class))),
        @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{userId}")
    ResponseEntity<UserActivityDto> findUserActivities(UUID loginId, UUID userId);
}
