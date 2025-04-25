package com.codeit.team2.monew.module.domain.interest.controller.docs;

import com.codeit.team2.monew.config.SwaggerTags.Descriptions;
import com.codeit.team2.monew.config.SwaggerTags.Tags;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
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

@Tag(name = Tags.INTEREST, description = Descriptions.INTEREST)
@RequestMapping("/api/interests")
public interface InterestControllerDocs {

    @Operation(
        summary = "관심사 등록",
        description = "새로운 관심사를 등록합니다. 요청자는 관심사 이름과 키워드 목록을 입력합니다.",
        requestBody = @RequestBody(
            content = @Content(schema = @Schema(implementation = InterestRegisterRequest.class))
        ),
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "등록 성공",
            content = @Content(schema = @Schema(implementation = InterestDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)"),
        @ApiResponse(responseCode = "409", description = "유사 관심사 중복"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    ResponseEntity<InterestDto> create(UUID userId, InterestRegisterRequest request);

    @Operation(
        summary = "관심사 수정",
        description = "기존의 관심사 정보를 수정합니다. 키워드만 변경할 수 있습니다.",
        requestBody = @RequestBody(
            content = @Content(schema = @Schema(implementation = InterestUpdateRequest.class))
        ),
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER),
            @Parameter(name = "interestId", description = "관심사 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = InterestDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)"),
        @ApiResponse(responseCode = "404", description = "관심사 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PatchMapping("/{interestId}")
    ResponseEntity<InterestDto> update(UUID userId, UUID id, InterestUpdateRequest request);

    @Operation(
        summary = "관심사 구독",
        description = "요청자가 특정 관심사를 구독합니다. 구독 중복 여부도 검증됩니다.",
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER),
            @Parameter(name = "interestId", description = "관심사 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "구독 성공",
            content = @Content(schema = @Schema(implementation = SubscriptionDto.class))),
        @ApiResponse(responseCode = "404", description = "관심사 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{interestId}/subscriptions")
    ResponseEntity<SubscriptionDto> subscription(UUID userId, UUID id);

    @Operation(
        summary = "관심사 물리 삭제",
        description = "요청자가 등록한 관심사를 영구적으로 삭제합니다. 복구는 불가능합니다.",
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER),
            @Parameter(name = "interestId", description = "관심사 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "관심사 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{interestId}")
    ResponseEntity<Void> delete(UUID userId, UUID id);

    @Operation(
        summary = "관심사 구독 취소",
        description = "요청자가 구독 중인 관심사를 구독 취소합니다.",
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER),
            @Parameter(name = "interestId", description = "관심사 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "구독 취소 성공"),
        @ApiResponse(responseCode = "404", description = "관심사 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{interestId}/subscriptions")
    ResponseEntity<Void> cancelSubscription(UUID userId, UUID id);
}
