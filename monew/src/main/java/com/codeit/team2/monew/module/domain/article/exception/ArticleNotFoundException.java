package com.codeit.team2.monew.module.domain.article.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;
import java.util.UUID;

public class ArticleNotFoundException extends BaseException {
    public ArticleNotFoundException(UUID articleId) {
        super(ArticleErrorCode.ARTICLE_NOT_FOUND, Map.of("articleId", articleId));
    }
}
