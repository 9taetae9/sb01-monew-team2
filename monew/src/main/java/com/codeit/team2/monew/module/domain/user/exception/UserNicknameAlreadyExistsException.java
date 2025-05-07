package com.codeit.team2.monew.module.domain.user.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;

public class UserNicknameAlreadyExistsException extends BaseException {

    public UserNicknameAlreadyExistsException(String nickname) {
        super(UserErrorCode.USER_NICKNAME_ALREADY_EXISTS, Map.of("nickname", nickname));
    }

}
