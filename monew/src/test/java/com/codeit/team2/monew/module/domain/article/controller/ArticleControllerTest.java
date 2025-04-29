package com.codeit.team2.monew.module.domain.article.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.team2.monew.module.domain.article.dto.ArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.dto.CursorPageResponseArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.request.ArticleOrderBy;
import com.codeit.team2.monew.module.domain.article.dto.request.CursorPageRequestArticleDto;
import com.codeit.team2.monew.module.domain.article.service.ArticleService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticleService articleService;

    @Test
    void shouldReturnArticleViewDto_whenNewUserViews_Article() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();

        ArticleViewDto dto = new ArticleViewDto(
            UUID.randomUUID(),
            userId,
            Instant.now(),
            articleId,
            "NAVER",
            "http://test.com",
            "test article",
            Instant.now(),
            "test summary",
            0,
            1
        );

        BDDMockito.given(articleService.createUserArticleView(userId, articleId))
            .willReturn(dto);

        // when && then
        mockMvc.perform(post("/api/articles/{articleId}/article-views", articleId)
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.articleId").value(articleId.toString()))
            .andExpect(jsonPath("$.viewedBy").value(userId.toString()))
            .andExpect(jsonPath("$.articleTitle").value("test article"))
            .andExpect(jsonPath("$.articleCommentCount").value(0))
            .andExpect(jsonPath("$.articleViewCount").value(1));
    }

    @Test
    void softDelete_success_should_toggleDeleted() throws Exception {
        // given
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BDDMockito.willDoNothing().given(articleService).softDelete(any());

        // when & then
        mockMvc.perform(delete("/api/articles/{articleId}", id)
                .header("Monew-Request-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    void findAll_success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        CursorPageRequestArticleDto requestDto = new CursorPageRequestArticleDto(
            null, null, null, null, null, ArticleOrderBy.publishDate, Direction.ASC, null, null, 10
        );
        CursorPageResponseArticleDto response = new CursorPageResponseArticleDto(
            List.of(
                new ArticleDto(UUID.randomUUID(), "", "", "", Instant.now(), "", 0L, 0L, false)),
            null, null, 1, 1L, false);

        when(articleService.findAll(eq(userId), any(CursorPageRequestArticleDto.class)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/articles")
                .header("Monew-Request-User-Id", userId.toString())
                .param("orderBy", "publishDate")
                .param("direction", "ASC")
                .param("size", "3")
                .param("limit", "3")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].id").exists())
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.hasNext").value(false));
    }
}
