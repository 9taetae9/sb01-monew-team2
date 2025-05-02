package com.codeit.team2.monew.module.domain.comment.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;
import java.util.UUID;

public class CommentNotFoundException extends BaseException {

    public CommentNotFoundException(UUID commentId) {
        super(CommentErrorCode.COMMENT_NOT_FOUND, Map.of("commentId", commentId));
    }
}
