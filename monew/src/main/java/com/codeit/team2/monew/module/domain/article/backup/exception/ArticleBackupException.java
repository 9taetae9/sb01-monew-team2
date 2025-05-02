package com.codeit.team2.monew.module.domain.article.backup.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import com.codeit.team2.monew.module.common.exception.ErrorCode;
import java.util.Map;

public class ArticleBackupException extends BaseException {

    public ArticleBackupException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ArticleBackupException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ArticleBackupException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }
}
