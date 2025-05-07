package com.codeit.team2.monew.module.domain.user.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;
import java.util.UUID;

public class UserUnauthorizedException extends BaseException {

    public UserUnauthorizedException(UUID loginId, UUID userId) {
        super(UserErrorCode.USER_UNAUTHORIZED,
            Map.of("loginId", loginId, "userId", userId));
    }

}
