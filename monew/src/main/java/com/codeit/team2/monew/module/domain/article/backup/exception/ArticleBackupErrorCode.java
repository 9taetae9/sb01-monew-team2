package com.codeit.team2.monew.module.domain.article.backup.exception;

import com.codeit.team2.monew.module.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ArticleBackupErrorCode implements ErrorCode {
    S3_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 저장소 접근 중 오류가 발생했습니다."),
    BACKUP_FILE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "백업 파일 파싱 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
