package com.codeit.team2.monew.module.domain.article.dto;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;

public record ArticleInterestCreateCommand(
    Article article,
    Interest interest
) {

}
