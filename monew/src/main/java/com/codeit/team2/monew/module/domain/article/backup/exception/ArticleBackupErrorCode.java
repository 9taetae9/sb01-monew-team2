package com.codeit.team2.monew.module.domain.article.backup.exception;

import com.codeit.team2.monew.module.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ArticleBackupErrorCode implements ErrorCode {
    S3_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3 저장소 접근 중 오류가 발생했습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "유효하지 않은 날짜 범위입니다. 시작일은 종료일보다 이전이어야 합니다."),
    BACKUP_FILE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "백업 파일 파싱 중 오류가 발생했습니다."),
    NO_BACKUP_FOUND(HttpStatus.NOT_FOUND, "요청한 날짜에 해당하는 백업 파일이 없습니다.");

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
