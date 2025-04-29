package com.codeit.team2.monew.module.domain.interest.code;

import com.codeit.team2.monew.module.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum InterestErrorCode implements ErrorCode {

    INTEREST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 관심사가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    InterestErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
