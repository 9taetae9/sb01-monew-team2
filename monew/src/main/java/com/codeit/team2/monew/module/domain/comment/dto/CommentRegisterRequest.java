package com.codeit.team2.monew.module.domain.comment.dto;

import java.util.UUID;

public record CommentRegisterRequest(
    UUID articleId,
    UUID userId,
    String content
) {

}
