package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import java.util.List;

public interface RssNewsClient {

    List<Article> fetchArticles();
}
