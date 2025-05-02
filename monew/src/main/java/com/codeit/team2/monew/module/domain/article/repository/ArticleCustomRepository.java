package com.codeit.team2.monew.module.domain.article.repository;

import com.codeit.team2.monew.module.domain.article.dto.request.ArticleSourceIn;
import com.codeit.team2.monew.module.domain.article.dto.request.CursorPageRequestArticleDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface ArticleCustomRepository {

    Slice<Article> findWithCursor(CursorPageRequestArticleDto request);
    

    long countFilteredTotalElements(String keyword, UUID interestId,
        List<ArticleSourceIn> sourceIn, Instant publishDateFrom, Instant publishDateTo);
}
