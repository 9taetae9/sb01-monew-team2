package com.codeit.team2.monew.module.domain.article.backup.service;

import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleBackupDto;
import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleRestoreResultDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleRestoreService {

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final int BATCH_SIZE = 200;

    /**
     * 시작 날짜부터 종료 날짜까지 기사 복원
     */
    public List<ArticleRestoreResultDto> restoreArticles(LocalDate from, LocalDate to) {
        List<ArticleRestoreResultDto> results = new ArrayList<>();
        log.info("Starting article restoration from {} to {}", from, to);

        // 각 날짜를 별도 트랜잭션으로 처리
        LocalDate current = from;
        while (!current.isAfter(to)) {
            LocalDate dateToProcess = current;
            try {
                // 각 날짜를 별도 트랜잭션으로 처리
                ArticleRestoreResultDto result = processDateWithNewTransaction(dateToProcess);
                results.add(result);
                log.info("Restored {} articles for date {}", result.restoredArticleCount(), dateToProcess);
            } catch (Exception e) {
                log.error("Failed to restore articles for date {}: {}", dateToProcess, e.getMessage(), e);
                results.add(new ArticleRestoreResultDto(
                    Instant.now(),
                    List.of(),
                    0L
                ));
            }
            current = current.plusDays(1);
        }

        log.info("Completed article restoration. Total dates processed: {}", results.size());
        return results;
    }

    /**
     * 날짜별 복원 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ArticleRestoreResultDto processDateWithNewTransaction(LocalDate date) {
        return restoreArticlesByDate(date);
    }

    /**
     * 특정 날짜 기사 복원
     */
    private ArticleRestoreResultDto restoreArticlesByDate(LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String prefix = "articles/" + formattedDate + "/";

        log.info("Restoring articles from date: {}", formattedDate);

        List<S3Object> allObjects = getAllS3Objects(prefix);
        if (allObjects.isEmpty()) {
            log.info("No backup files found for date: {}", formattedDate);
            return new ArticleRestoreResultDto(
                Instant.now(),
                List.of(),
                0L
            );
        }

        Set<String> existingUrls = loadExistingUrls();
        log.info("Loaded {} existing URLs from database", existingUrls.size());

        // 복원된 기사 ID 목록
        List<UUID> restoredIds = new ArrayList<>();
        int processedFiles = 0;
        int errorCount = 0;
        int totalRestored = 0;

        // 각 백업 파일 순차 처리
        for (S3Object s3Object : allObjects) {
            try {
                log.info("Processing backup file: {}", s3Object.key());
                // 각 파일 새 트랜잭션으로 처리
                ProcessFileResult result = processFileWithNewTransaction(s3Object, existingUrls);

                if (result.success) {
                    restoredIds.addAll(result.savedIds);
                    totalRestored += result.savedIds.size();
                    // 기존 URL 리스트 업데이트
                    existingUrls.addAll(result.processedUrls);
                } else {
                    errorCount++;
                }

                processedFiles++;

                if (processedFiles % 5 == 0) {
                    log.info("Processed {} files, restored {} articles so far",
                        processedFiles, totalRestored);
                }
            } catch (Exception e) {
                log.error("Error processing backup file {}: {}", s3Object.key(), e.getMessage(), e);
                errorCount++;
            }
        }

        if (errorCount > 0) {
            log.warn("Completed with {} file errors for date {}", errorCount, formattedDate);
        }

        log.info("Restored {} articles for date {} from {} files",
            restoredIds.size(), formattedDate, allObjects.size());

        return new ArticleRestoreResultDto(
            Instant.now(),
            restoredIds,
            (long) restoredIds.size()
        );
    }

    /**
     * 파일 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ProcessFileResult processFileWithNewTransaction(S3Object s3Object, Set<String> existingUrls) {
        try {
            List<Article> articles = processBackupFile(s3Object, existingUrls);
            if (!articles.isEmpty()) {
                List<UUID> savedIds = batchInsertArticles(articles);

                Set<String> processedUrls = articles.stream()
                    .map(Article::getSourceUrl)
                    .collect(Collectors.toSet());

                return new ProcessFileResult(true, savedIds, processedUrls);
            }
            return new ProcessFileResult(true, List.of(), Set.of());
        } catch (Exception e) {
            log.error("Failed to process file {}: {}", s3Object.key(), e.getMessage(), e);
            return new ProcessFileResult(false, List.of(), Set.of());
        }
    }

    /**
     * 기사 일괄 삽입: BatchPreparedStatementSetter 사용
     */
    private List<UUID> batchInsertArticles(List<Article> articles) {
        final List<UUID> savedIds = new ArrayList<>();
        final Timestamp now = Timestamp.from(Instant.now());

        String sql = "INSERT INTO articles (id, title, source, source_url, summary, view_count, published_date, deleted, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT (id) DO NOTHING";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Article article = articles.get(i);
                UUID id = article.getId();
                savedIds.add(id);

                ps.setObject(1, id); // UUID
                ps.setString(2, article.getTitle());
                ps.setString(3, article.getSource());
                ps.setString(4, article.getSourceUrl());
                ps.setString(5, article.getSummary());
                ps.setLong(6, article.getViewCount());
                ps.setTimestamp(7, article.getPublishedDate() != null ?
                    Timestamp.from(article.getPublishedDate()) : null);
                ps.setBoolean(8, article.getDeleted());
                ps.setTimestamp(9, now);
                ps.setTimestamp(10, null);
            }

            @Override
            public int getBatchSize() {
                return articles.size();
            }
        });

        return savedIds;
    }

    /**
     * 파일 처리 결과
     */
    private static class ProcessFileResult {
        final boolean success;
        final List<UUID> savedIds;
        final Set<String> processedUrls;

        ProcessFileResult(boolean success, List<UUID> savedIds, Set<String> processedUrls) {
            this.success = success;
            this.savedIds = savedIds;
            this.processedUrls = processedUrls;
        }
    }

    /**
     * 기존 URL 목록 로드
     */
    private Set<String> loadExistingUrls() {
        List<String> urls = jdbcTemplate.queryForList(
            "SELECT source_url FROM articles", String.class);
        return new HashSet<>(urls);
    }

    /**
     * 개별 백업 파일을 처리하고 복원할 기사 목록 반환
     */
    private List<Article> processBackupFile(S3Object s3Object, Set<String> existingUrls) {
        List<Article> articlesToRestore = new ArrayList<>();

        try (ResponseInputStream<GetObjectResponse> responseStream = s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Object.key())
                .build())) {

            List<ArticleBackupDto> backupDtos;
            try {
                backupDtos = objectMapper.readValue(
                    responseStream,
                    new TypeReference<List<ArticleBackupDto>>() {});
                log.info("Parsed {} articles from backup file {}",
                    backupDtos.size(), s3Object.key());
            } catch (IOException e) {
                log.error("Failed to parse backup file {}: {}", s3Object.key(), e.getMessage());
                return articlesToRestore;
            }

            int skippedCount = 0;

            // 각 기사 처리
            for (ArticleBackupDto dto : backupDtos) {
                try {
                    if (existingUrls.contains(dto.sourceUrl())) {
                        skippedCount++;
                        continue;
                    }

                    Article article = new Article(
                        dto.title(),
                        dto.source(),
                        dto.sourceUrl(),
                        dto.summary(),
                        new HashSet<>(), // 관심사 관계 추후 고려
                        dto.viewCount(),
                        dto.publishedDate(),
                        dto.deleted()
                    );

                    // 기본 키 설정 - 백업 데이터의 id를 그대로 사용
                    article.setId(dto.id()); // BaseEntity에 setter 사용(추후 변경 고려)

                    articlesToRestore.add(article);
                } catch (Exception e) {
                    log.error("Error processing article {}: {}",
                        dto.sourceUrl(), e.getMessage(), e);
                }
            }

            log.info("File processing summary - To restore: {}, Skipped: {}, Total: {}",
                articlesToRestore.size(), skippedCount, backupDtos.size());

        } catch (Exception e) {
            log.error("Error reading S3 object {}: {}", s3Object.key(), e.getMessage(), e);
        }

        return articlesToRestore;
    }

    /**
     * S3 페이지네이션
     * @param prefix
     * @return
     */
    private List<S3Object> getAllS3Objects(String prefix) {

        List<S3Object> allObjects = new ArrayList<>();
        String continuationToken = null;

        do {
            ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix);

            if (continuationToken != null) {
                requestBuilder.continuationToken(continuationToken);
            }

            ListObjectsV2Response response = s3Client.listObjectsV2(requestBuilder.build());
            allObjects.addAll(response.contents());
            continuationToken = response.nextContinuationToken();
        } while (continuationToken != null);

        return allObjects;
    }
}
