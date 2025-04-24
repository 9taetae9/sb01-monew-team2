package com.codeit.team2.monew.module.domain.comment.controller.docs;

import com.codeit.team2.monew.module.domain.comment.dto.CommentLikeDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "댓글 좋아요 관리", description = "댓글 좋아요 관련 API")
@RequestMapping("/api/comments/{commentId}/comment-likes")
public interface CommentLikeControllerDocs {

    @Operation(
        summary = "댓글 좋아요",
        description = "댓글에 좋아요를 등록합니다.",
        parameters = {
            @Parameter(name = "commentId", description = "댓글 ID"),
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 좋아요 성공",
            content = @Content(schema = @Schema(implementation = CommentLikeDto.class))),
        @ApiResponse(responseCode = "404", description = "댓글 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    ResponseEntity<CommentLikeDto> like(UUID commentId, UUID userId);

    @Operation(
        summary = "댓글 좋아요 취소",
        description = "댓글의 좋아요를 취소합니다.",
        parameters = {
            @Parameter(name = "commentId", description = "댓글 ID"),
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "댓글 좋아요 취소 성공"),
        @ApiResponse(responseCode = "404", description = "관심사 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping
    ResponseEntity<Void> unlike(UUID commentId, UUID userId);
}
