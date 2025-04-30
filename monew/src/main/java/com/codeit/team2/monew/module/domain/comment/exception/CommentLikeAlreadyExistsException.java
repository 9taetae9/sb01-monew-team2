package com.codeit.team2.monew.module.domain.comment.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import com.codeit.team2.monew.module.domain.comment.code.CommentErrorCode;
import java.util.Map;
import java.util.UUID;

public class CommentLikeAlreadyExistsException extends BaseException {
    public CommentLikeAlreadyExistsException(UUID commentId, UUID userId) {
        super(CommentErrorCode.COMMENT_LIKE_ALREADY_EXISTS,
            Map.of("commentId", commentId, "userId", userId));
    }
}
