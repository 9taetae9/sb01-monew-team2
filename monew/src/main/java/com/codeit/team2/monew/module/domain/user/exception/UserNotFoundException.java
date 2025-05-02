package com.codeit.team2.monew.module.domain.user.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(UUID userId) {
        super(UserErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }
}
