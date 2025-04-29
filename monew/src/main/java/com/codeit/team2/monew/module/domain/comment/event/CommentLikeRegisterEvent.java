package com.codeit.team2.monew.module.domain.comment.event;

import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;

public record CommentLikeRegisterEvent(
    CommentLike commentLike
) {

}
