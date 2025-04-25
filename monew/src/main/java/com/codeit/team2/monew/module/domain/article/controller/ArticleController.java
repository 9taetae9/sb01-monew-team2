package com.codeit.team2.monew.module.domain.article.controller;


import com.codeit.team2.monew.module.domain.article.controller.docs.ArticleControllerDocs;
import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.dto.CursorPageResponseArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.request.CursorPageRequestArticleDto;
import com.codeit.team2.monew.module.domain.article.service.ArticleService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController implements ArticleControllerDocs {

    private final ArticleService articleService;


    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> createArticleView(@PathVariable UUID articleId,
        @RequestHeader("Monew-Request-User-ID") UUID userId) {
        ArticleViewDto dto = articleService.createUserArticleView(userId, articleId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> softDeleteArticle(@PathVariable UUID articleId) {
        articleService.softDelete(articleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{articleId}/hard")
    public ResponseEntity<Void> hardDeleteArticle(@PathVariable UUID articleId) {
        articleService.hardDelete(articleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public ResponseEntity<CursorPageResponseArticleDto> findAll(
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        @Valid @ModelAttribute CursorPageRequestArticleDto cursorPageRequestArticleDto) {
        log.info("Start - ArticleController/findAll");
        CursorPageResponseArticleDto result = articleService.findAll(userId,
            cursorPageRequestArticleDto);
        log.info("Complete - ArticleController/findAll");
        return ResponseEntity.ok().body(result);
    }

}
