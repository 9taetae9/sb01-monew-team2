package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import java.util.List;

/**
 * 외부 news api 요청에 활용될 공통 인터페이스
 */
public interface NewsClient {

    List<Article> fetchArticles(FetchCommand cmd);
}
