package com.codeit.team2.monew.module.domain.comment.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;

public class CommentInvalidRequestException extends BaseException {
    public CommentInvalidRequestException(String message) {
        super(CommentErrorCode.COMMENT_INVALID_REQUEST, message);
    }
}
