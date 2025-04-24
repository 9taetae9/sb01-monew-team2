package com.codeit.team2.monew.module.domain.article.service;

import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.dto.CursorPageResponseArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.request.CursorPageRequestArticleDto;
import java.util.UUID;

public interface ArticleService {

    ArticleViewDto createUserArticleView(UUID userId, UUID articleId);

    void softDelete(UUID articleId);

    void hardDelete(UUID articleId);

    CursorPageResponseArticleDto findAll(UUID userId,
        CursorPageRequestArticleDto cursorPageRequestArticleDto);
}
