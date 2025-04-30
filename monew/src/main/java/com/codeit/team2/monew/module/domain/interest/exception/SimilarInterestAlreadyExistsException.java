package com.codeit.team2.monew.module.domain.interest.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import com.codeit.team2.monew.module.domain.interest.code.InterestErrorCode;
import java.util.Map;

public class SimilarInterestAlreadyExistsException extends BaseException {

    public SimilarInterestAlreadyExistsException(String name) {
        super(InterestErrorCode.SIMILAR_INTEREST_ALREADY_EXISTS, Map.of("name", name));
    }
}
