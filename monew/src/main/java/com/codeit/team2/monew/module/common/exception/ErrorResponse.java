package com.codeit.team2.monew.module.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.Instant;
import java.util.Map;

@JsonInclude(Include.NON_EMPTY)
public record ErrorResponse(
    Instant timestamp,
    String code,
    int status,
    String exceptionType,
    String message,
    Map<String, Object> details
) {

    public static ErrorResponse from(BaseException ex) {
        return new ErrorResponse(
            Instant.now(),
            ex.getErrorCode().toString(),
            ex.getErrorCode().getHttpStatus().value(),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            ex.getDetails()
        );
    }

    public static ErrorResponse from(Exception ex, ErrorCode errorCode) {
        return new ErrorResponse(
            Instant.now(),
            errorCode.toString(),
            errorCode.getHttpStatus().value(),
            ex.getClass().getSimpleName(),
            errorCode.getMessage(),
            null
        );
    }

    public static ErrorResponse of(String code, int status, String exceptionType,
        String message, Map<String, Object> details) {
        return new ErrorResponse(
            Instant.now(),
            code,
            status,
            exceptionType,
            message,
            details
        );
    }

}
