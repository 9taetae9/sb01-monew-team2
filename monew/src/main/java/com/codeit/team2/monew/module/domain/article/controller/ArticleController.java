package com.codeit.team2.monew.module.domain.article.controller;


import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.service.ArticleService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> createArticleView(@PathVariable UUID articleId,
        @RequestHeader("MoNew-Request-User-ID") UUID userId) {
        ArticleViewDto dto = articleService.createUserArticleView(userId, articleId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> softDeleteArticle(@PathVariable UUID articleId) {
        articleService.softDelete(articleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{articleId}/hard")
    public ResponseEntity<?> hardDeleteArticle(@PathVariable UUID articleId) {
        articleService.hardDelete(articleId);
        return ResponseEntity.noContent().build();
    }
}
