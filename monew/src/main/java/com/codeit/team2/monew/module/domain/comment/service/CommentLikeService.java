package com.codeit.team2.monew.module.domain.comment.service;

import java.util.UUID;

public interface CommentLikeService {

    CommentService like(UUID commentId, UUID userId);

    void unlike(UUID commentId, UUID userId);

}
