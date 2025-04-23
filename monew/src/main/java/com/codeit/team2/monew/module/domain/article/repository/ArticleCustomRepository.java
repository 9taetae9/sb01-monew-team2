package com.codeit.team2.monew.module.domain.article.repository;

import com.codeit.team2.monew.module.domain.article.dto.request.ArticleSourceIn;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

public interface ArticleCustomRepository {

    Page<Article> findByPublishDate(
        String keyword,
        UUID interestId,
        List<ArticleSourceIn> sourceIn,
        Instant publishDateFrom,
        Instant publishDateTo,
        Direction direction,
        String cursor,
        Instant after,
        Pageable pageable
    );

    Page<Article> findByViewCount(
        String keyword,
        UUID interestId,
        List<ArticleSourceIn> sourceIn,
        Instant publishDateFrom,
        Instant publishDateTo,
        Direction direction,
        String cursor,
        Instant after,
        Pageable pageable
    );

    Page<Article> findByCommentCount(
        String keyword,
        UUID interestId,
        List<ArticleSourceIn> sourceIn,
        Instant publishDateFrom,
        Instant publishDateTo,
        Direction direction,
        String cursor,
        Instant after,
        Pageable pageable
    );

    long countFilteredTotalElements(String keyword, UUID interestId,
        List<ArticleSourceIn> sourceIn, Instant publishDateFrom, Instant publishDateTo);
}
