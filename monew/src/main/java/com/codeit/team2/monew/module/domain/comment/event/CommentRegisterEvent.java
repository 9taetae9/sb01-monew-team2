package com.codeit.team2.monew.module.domain.comment.event;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.user.entity.User;

public record CommentRegisterEvent(
    Comment comment,
    Article article,
    User user
) {

}
