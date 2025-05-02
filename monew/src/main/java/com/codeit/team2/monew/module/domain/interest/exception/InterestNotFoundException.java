package com.codeit.team2.monew.module.domain.interest.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import com.codeit.team2.monew.module.common.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class InterestNotFoundException extends BaseException {

    public InterestNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InterestNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public InterestNotFoundException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public InterestNotFoundException(UUID id) {
        super(InterestErrorCode.INTEREST_NOT_FOUND, Map.of("id", id));
    }
}
