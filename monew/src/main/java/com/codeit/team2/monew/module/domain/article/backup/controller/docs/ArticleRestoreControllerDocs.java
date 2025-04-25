package com.codeit.team2.monew.module.domain.article.backup.controller.docs;

import com.codeit.team2.monew.config.SwaggerTags.Descriptions;
import com.codeit.team2.monew.config.SwaggerTags.Tags;
import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleRestoreResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = Tags.ARTICLE, description = Descriptions.ARTICLE)
@RequestMapping("/api/articles")
public interface ArticleRestoreControllerDocs {

    @Operation(
        summary = "뉴스 복구",
        description = "유실된 뉴스 기사를 복구",
        parameters = {
            @Parameter(name = "from", description = "복원 시작일 (yyyy-MM-dd)", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "to", description = "복원 종료일 (yyyy-MM-dd)", required = true, in = ParameterIn.QUERY)
        }
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "복구 성공",
            content = @Content(schema = @Schema(implementation = ArticleRestoreResultDto.class))
        ),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/restore")
    ResponseEntity<List<ArticleRestoreResultDto>> restoreArticles(LocalDate from, LocalDate to);
}
