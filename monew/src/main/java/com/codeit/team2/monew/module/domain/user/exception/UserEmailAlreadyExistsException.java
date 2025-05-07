package com.codeit.team2.monew.module.domain.user.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;

public class UserEmailAlreadyExistsException extends BaseException {

    public UserEmailAlreadyExistsException(String email) {
        super(UserErrorCode.USER_EMAIL_ALREADY_EXISTS, Map.of("email", email));
    }
}
