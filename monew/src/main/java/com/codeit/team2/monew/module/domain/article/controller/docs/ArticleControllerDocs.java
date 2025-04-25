package com.codeit.team2.monew.module.domain.article.controller.docs;

import com.codeit.team2.monew.config.SwaggerTags.Descriptions;
import com.codeit.team2.monew.config.SwaggerTags.Tags;
import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.dto.CursorPageResponseArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.request.CursorPageRequestArticleDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = Tags.ARTICLE, description = Descriptions.ARTICLE)
@RequestMapping("/api/articles")
public interface ArticleControllerDocs {

    @Operation(
        summary = "기사 뷰 등록",
        description = "특정 뉴스 기사를 요청자가 조회했음을 기록합니다.",
        parameters = {
            @Parameter(name = "articleId", description = "기사 ID"),
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "기사 뷰 등록 성공",
            content = @Content(schema = @Schema(implementation = ArticleViewDto.class))),
        @ApiResponse(responseCode = "404", description = "댓글 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{articleId}/article-views")
    ResponseEntity<ArticleViewDto> createArticleView(UUID articleId, UUID userId);

    @Operation(
        summary = "뉴스 기사 논리 삭제",
        description = "뉴스 기사를 논리적으로 삭제합니다.",
        parameters = {
            @Parameter(name = "articleId", description = "뉴스 기사 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "논리 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{articleId}")
    ResponseEntity<?> softDeleteArticle(UUID articleId);

    @Operation(
        summary = "뉴스 기사 물리 삭제",
        description = "뉴스 기사를 완전히 삭제합니다. 이 작업은 되돌릴 수 없습니다.",
        parameters = {
            @Parameter(name = "articleId", description = "뉴스 기사 ID")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{articleId}/hard")
    ResponseEntity<?> hardDeleteArticle(UUID articleId);

    @Operation(
        summary = "뉴스 기사 목록 조회",
        description = "관심사 및 요청자 설정에 따라 뉴스 기사 목록을 커서 기반 페이지네이션으로 조회합니다.",
        parameters = {
            @Parameter(name = "Monew-Request-User-ID", description = "요청자 ID", in = ParameterIn.HEADER),
            @Parameter(name = "articleFindRequest", description = "기사 조회 조건 (정렬 기준, 커서 등)")
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = CursorPageResponseArticleDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("")
    ResponseEntity<CursorPageResponseArticleDto> findAll(UUID userId, CursorPageRequestArticleDto cursorPageRequestArticleDto);
}
