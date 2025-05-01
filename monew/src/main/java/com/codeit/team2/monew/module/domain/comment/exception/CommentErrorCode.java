package com.codeit.team2.monew.module.domain.comment.exception;

import com.codeit.team2.monew.module.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글 정보를 찾을 수 없습니다."),
    COMMENT_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "댓글에 대한 권한이 없습니다."),
    COMMENT_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 댓글 요청입니다."),
    COMMENT_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글 좋아요 정보를 찾을 수 없습니다."),
    COMMENT_LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 좋아요한 댓글입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
