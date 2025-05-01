package com.codeit.team2.monew.module.domain.subscription.exception;

import com.codeit.team2.monew.module.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum SubscriptionErrorCode implements ErrorCode {

    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "구독 상태가 아닙니다."),
    DUPLICATE_SUBSCRIPTION(HttpStatus.CONFLICT, "이미 구독 중입니다.");

    private final HttpStatus status;
    private final String message;

    SubscriptionErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
