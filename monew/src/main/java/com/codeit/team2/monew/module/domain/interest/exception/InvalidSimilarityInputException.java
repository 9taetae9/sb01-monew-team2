package com.codeit.team2.monew.module.domain.interest.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;

public class InvalidSimilarityInputException extends BaseException {

    public InvalidSimilarityInputException(String message) {
        super(InterestErrorCode.INVALID_SIMILARITY_INPUT, message);
    }
}
