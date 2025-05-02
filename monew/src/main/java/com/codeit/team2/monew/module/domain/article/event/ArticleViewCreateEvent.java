package com.codeit.team2.monew.module.domain.article.event;

import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.user.entity.User;

public record ArticleViewCreateEvent(
    ArticleView articleView,
    User user
) {

}
