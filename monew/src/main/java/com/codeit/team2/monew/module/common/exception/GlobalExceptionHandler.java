package com.codeit.team2.monew.module.common.exception;

import com.codeit.team2.monew.module.domain.interest.exception.InvalidSimilarityInputException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSimilarityInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSimilarityInput(
        InvalidSimilarityInputException ex) {
        log.error("Invalid similarity input", ex);

        ErrorResponse errorResponse = ErrorResponse.from(ex);
        return createErrorResponseEntity(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex) {

        ErrorCode errorCode = CommonErrorCode.INVALID_INPUT_VALUE;
        FieldError fieldError = ex.getFieldErrors().get(0);

        log.info("Validation failed: {} - {}", fieldError.getField(),
            fieldError.getRejectedValue());

        ErrorResponse errorResponse = ErrorResponse.of(
            errorCode.toString(),
            errorCode.getHttpStatus().value(),
            ex.getClass().getSimpleName(),
            fieldError.getDefaultMessage(),
            Map.of(fieldError.getField(), fieldError.getRejectedValue())
        );

        return createErrorResponseEntity(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupport(
        HttpRequestMethodNotSupportedException ex) {

        log.info("Request method not supported: {}", ex.getMethod());

        ErrorResponse errorResponse = ErrorResponse.from(ex, CommonErrorCode.METHOD_NOT_ALLOWED);

        return createErrorResponseEntity(errorResponse);
    }

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BaseException ex) {

        ErrorResponse errorResponse = ErrorResponse.from(ex);

        return createErrorResponseEntity(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {

        log.error("Exception", ex);

        ErrorResponse errorResponse = ErrorResponse.from(ex, CommonErrorCode.INTERNAL_SERVER_ERROR);

        return createErrorResponseEntity(errorResponse);
    }

    private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorResponse errorResponse) {

        return ResponseEntity
            .status(errorResponse.status())
            .body(errorResponse);
    }
}
