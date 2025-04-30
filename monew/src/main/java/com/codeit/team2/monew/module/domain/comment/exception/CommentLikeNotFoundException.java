package com.codeit.team2.monew.module.domain.comment.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import com.codeit.team2.monew.module.domain.comment.code.CommentErrorCode;
import java.util.Map;
import java.util.UUID;

public class CommentLikeNotFoundException extends BaseException {
    public CommentLikeNotFoundException(UUID commentId, UUID userId) {
        super(CommentErrorCode.COMMENT_LIKE_NOT_FOUND,
            Map.of("commentId", commentId, "userId", userId));
    }
}
