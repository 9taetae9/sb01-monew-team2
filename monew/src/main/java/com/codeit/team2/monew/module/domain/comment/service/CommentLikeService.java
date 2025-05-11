package com.codeit.team2.monew.module.domain.comment.service;

import com.codeit.team2.monew.module.domain.comment.dto.CommentLikeDto;
import java.util.UUID;

public interface CommentLikeService {

    CommentLikeDto like(UUID commentId, UUID userId);

    void unlike(UUID commentId, UUID userId);

}
