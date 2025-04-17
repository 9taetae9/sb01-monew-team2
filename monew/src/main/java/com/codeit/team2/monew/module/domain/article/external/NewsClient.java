package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import java.util.List;

public interface NewsClient {

    List<Article> fetchArticles(FetchCommand cmd);
}
