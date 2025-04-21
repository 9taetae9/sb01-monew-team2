package com.codeit.team2.monew.module.domain.comment.service;

import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import java.util.UUID;

public interface CommentLikeService {

    CommentLike like(UUID commentId, UUID userId);

    void unlike(UUID commentId, UUID userId);

}
