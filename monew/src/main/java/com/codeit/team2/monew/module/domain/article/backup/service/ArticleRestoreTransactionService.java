package com.codeit.team2.monew.module.domain.article.backup.service;


import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleRestoreResultDto;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * 복구 서비스 트랜잭션 처리 담당
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleRestoreTransactionService {

    /**
     * 날짜별 복원 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ArticleRestoreResultDto processDateWithNewTransaction(
        ArticleRestoreService restoreService, LocalDate date) {
        try {
            ArticleRestoreResultDto result = restoreService.restoreArticlesByDate(date);
            log.debug("Transaction completed successfully for date: {}", date);
            return result;
        } catch (Exception e) {
            log.error("Transaction failed for date {}: {}", date, e.getMessage());
            throw e;
        }
    }

    /**
     * 파일 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ArticleRestoreService.ProcessFileResult processFileWithNewTransaction(
        ArticleRestoreService restoreService, S3Object s3Object, Set<String> existingUrls) {
        log.debug("Starting new transaction for file: {}", s3Object.key());
        try {
            ArticleRestoreService.ProcessFileResult result =
                restoreService.processBackupFileWithTransaction(s3Object, existingUrls);
            log.debug("Transaction completed for file: {}", s3Object.key());
            return result;
        } catch (Exception e) {
            log.error("Transaction failed for file {}: {}", s3Object.key(), e.getMessage());
            throw e;
        }
    }
}
