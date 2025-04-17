package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.FetchArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import java.util.List;

public interface NewsClient {
    List<FetchArticleDto> fetchArticles(FetchCommand cmd);
}
