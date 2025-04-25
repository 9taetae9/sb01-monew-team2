package com.codeit.team2.monew.module.domain.article.backup.service;


import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleRestoreResultDto;
import java.time.LocalDate;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * 복구 서비스 트랜잭션 처리 담당
 */
@Service
@RequiredArgsConstructor
public class ArticleRestoreTransactionService {

    /**
     * 날짜별 복원 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ArticleRestoreResultDto processDateWithNewTransaction(
        ArticleRestoreService restoreService, LocalDate date) {
        return restoreService.restoreArticlesByDate(date);
    }

    /**
     * 파일 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ArticleRestoreService.ProcessFileResult processFileWithNewTransaction(
        ArticleRestoreService restoreService, S3Object s3Object, Set<String> existingUrls) {
        return restoreService.processBackupFileWithTransaction(s3Object, existingUrls);
    }
}
