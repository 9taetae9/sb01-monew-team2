package com.codeit.team2.monew.module.domain.article.backup.controller;


import com.codeit.team2.monew.module.domain.article.backup.controller.docs.ArticleRestoreControllerDocs;
import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleRestoreResultDto;
import com.codeit.team2.monew.module.domain.article.backup.service.ArticleRestoreService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleRestoreController implements ArticleRestoreControllerDocs {

    private final ArticleRestoreService articleRestoreService;

    @GetMapping("/restore")
    public ResponseEntity<List<ArticleRestoreResultDto>> restoreArticles(
        @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        log.info("Start - ArticleRestoreController/restoreArticles: Date Range {} to {}", from, to);
        List<ArticleRestoreResultDto> results = articleRestoreService.restoreArticles(from, to);

        log.info("Complete - ArticleRestoreController/restoreArticles");

        return ResponseEntity.ok(results);
    }
}
