package com.codeit.team2.monew.module.domain.comment.event;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import java.util.UUID;

public record CommentUpdateEvent(
    Comment comment,
    UUID userId
) {

}
