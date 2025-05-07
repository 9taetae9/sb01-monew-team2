package com.codeit.team2.monew.module.domain.article.backup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleBackupDto;
import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleRestoreResultDto;
import com.codeit.team2.monew.module.domain.article.backup.exception.ArticleBackupErrorCode;
import com.codeit.team2.monew.module.domain.article.backup.exception.ArticleBackupException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ArticleRestoreServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ArticleRestoreTransactionService transactionService;

    private ArticleRestoreService restoreService;

    @Captor
    private ArgumentCaptor<LocalDate> localDateCaptor;

    @Captor
    private ArgumentCaptor<S3Object> s3ObjectCaptor;

    private final LocalDate TEST_DATE = LocalDate.of(2025, 5, 7);

    @BeforeEach
    void setUp() {
        restoreService = new ArticleRestoreService(s3Client, objectMapper, jdbcTemplate, transactionService);

        lenient().when(jdbcTemplate.queryForList(anyString(), eq(String.class)))
            .thenReturn(Collections.emptyList());
    }

    private S3Object createS3Object(String key) {
        return S3Object.builder().key(key).build();
    }

    private ArticleBackupDto createArticleBackupDto(UUID id, String title, String url) {
        return new ArticleBackupDto(
            id,
            title,
            "출처",
            url,
            "요약",
            10L,
            Instant.now(),
            false,
            Set.of(UUID.randomUUID()),
            Instant.now()
        );
    }

    @Nested
    @DisplayName("날짜별 기사 복원 테스트")
    class RestoreArticlesByDateTests {

        @Test
        @DisplayName("S3 접근 중 오류가 발생 시 - 예외 발생")
        void restoreArticlesByDate_S3AccessError() {
            when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenThrow(S3Exception.builder().message("S3 접근 오류").build());

            ArticleBackupException exception = assertThrows(
                ArticleBackupException.class,
                () -> restoreService.restoreArticlesByDate(TEST_DATE)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ArticleBackupErrorCode.S3_ACCESS_ERROR);
            verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
            verify(transactionService, never()).processFileWithNewTransaction(any(), any(), anySet());
        }

        @Test
        @DisplayName("AWS SDK 클라이언트 예외 - ArticleBackupException 변환")
        void restoreArticlesByDate_SdkClientException() {
            when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenThrow(SdkClientException.builder().message("SDK 클라이언트 오류").build());

            ArticleBackupException exception = assertThrows(
                ArticleBackupException.class,
                () -> restoreService.restoreArticlesByDate(TEST_DATE)
            );

            assertThat(exception.getErrorCode()).isEqualTo(ArticleBackupErrorCode.S3_ACCESS_ERROR);
            verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
            verify(transactionService, never()).processFileWithNewTransaction(any(), any(), anySet());
        }

        @Test
        @DisplayName("S3 객체 없을 때 - 빈 결과 반환")
        void restoreArticlesByDate_NoObjectsFound() {
            ListObjectsV2Response emptyResponse = ListObjectsV2Response.builder()
                .contents(Collections.emptyList())
                .build();

            when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(emptyResponse);

            ArticleRestoreResultDto result = restoreService.restoreArticlesByDate(TEST_DATE);

            assertThat(result).isNotNull();
            assertThat(result.restoredArticleIds()).isEmpty();
            assertThat(result.restoredArticleCount()).isEqualTo(0L);
            assertThat(result.restoreDate()).isNotNull();

            verify(s3Client).listObjectsV2(any(ListObjectsV2Request.class));
            verify(transactionService, never()).processFileWithNewTransaction(any(), any(), anySet());
        }

        @Test
        @DisplayName("파일 처리 중 예외 발생 시 해당 날짜는 빈 결과를 반환")
        void singleFileProcessing_ExceptionHandling() {
            S3Object s3Object = createS3Object("articles/2025-05-07/batch_uuid.json");

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(List.of(s3Object))
                .build();

            when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(response);

            when(transactionService.processFileWithNewTransaction(any(), any(S3Object.class), anySet()))
                .thenThrow(new ArticleBackupException(ArticleBackupErrorCode.S3_ACCESS_ERROR));

            ArticleRestoreResultDto result = restoreService.restoreArticlesByDate(TEST_DATE);

            assertThat(result).isNotNull();
            assertThat(result.restoredArticleIds()).isEmpty();
            assertThat(result.restoredArticleCount()).isZero();

            verify(transactionService).processFileWithNewTransaction(any(), any(S3Object.class), anySet());
        }
    }

    @Nested
    @DisplayName("날짜 범위 기사 복원 테스트")
    class DateRangeRestoreTests {

        @Test
        @DisplayName("백업 파일 복구 - 성공: 특정 날짜부터 특정 날짜까지 처리")
        void restoreArticles_SuccessForDateRange() {
            LocalDate fromDate = LocalDate.of(2025, 5, 1);
            LocalDate toDate = LocalDate.of(2025, 5, 3); // 3일간

            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            UUID id3 = UUID.randomUUID();

            ArticleRestoreResultDto result1 = new ArticleRestoreResultDto(
                Instant.now(), List.of(id1), 1L);
            ArticleRestoreResultDto result2 = new ArticleRestoreResultDto(
                Instant.now(), List.of(id2), 1L);
            ArticleRestoreResultDto result3 = new ArticleRestoreResultDto(
                Instant.now(), List.of(id3), 1L);

            when(transactionService.processDateWithNewTransaction(any(), any(LocalDate.class)))
                .thenReturn(result1, result2, result3);

            List<ArticleRestoreResultDto> results = restoreService.restoreArticles(fromDate, toDate);

            assertThat(results).hasSize(3);
            assertThat(results.get(0).restoredArticleIds()).contains(id1);
            assertThat(results.get(1).restoredArticleIds()).contains(id2);
            assertThat(results.get(2).restoredArticleIds()).contains(id3);

            verify(transactionService, times(3)).processDateWithNewTransaction(any(), localDateCaptor.capture());

            List<LocalDate> capturedDates = localDateCaptor.getAllValues();
            assertThat(capturedDates).containsExactly(
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 2),
                LocalDate.of(2025, 5, 3)
            );
        }

        @Test
        @DisplayName("트랜잭션 동작 검증 - 한 날짜의 처리 실패가 다른 날짜 처리에 영향을 주지 않음")
        void restoreArticles_ContinuesAfterError() {
            LocalDate fromDate = LocalDate.of(2025, 5, 1);
            LocalDate toDate = LocalDate.of(2025, 5, 3); // 3일간

            UUID id1 = UUID.randomUUID();
            UUID id3 = UUID.randomUUID();

            ArticleRestoreResultDto result1 = new ArticleRestoreResultDto(
                Instant.now(), List.of(id1), 1L);
            ArticleRestoreResultDto result3 = new ArticleRestoreResultDto(
                Instant.now(), List.of(id3), 1L);

            // 두번째 날짜에서 예외 발생
            when(transactionService.processDateWithNewTransaction(any(), any(LocalDate.class)))
                .thenReturn(result1)
                .thenThrow(new ArticleBackupException(ArticleBackupErrorCode.S3_ACCESS_ERROR))
                .thenReturn(result3);

            List<ArticleRestoreResultDto> results = restoreService.restoreArticles(fromDate, toDate);

            assertThat(results).hasSize(3); // 실패한 날짜를 포함하여 3개
            assertThat(results.get(0).restoredArticleIds()).contains(id1);
            assertThat(results.get(1).restoredArticleIds()).isEmpty(); // 실패한 날짜: 빈 결과
            assertThat(results.get(2).restoredArticleIds()).contains(id3);
        }
    }

    @Nested
    @DisplayName("백업 파일 처리 테스트")
    class ProcessBackupFileTests {

        @Test
        @DisplayName("백업 파일 복구 - 성공")
        void processBackupFileWithTransaction_Success() throws Exception {
            S3Object s3Object = createS3Object("articles/2025-05-07/batch_uuid.json");

            Set<String> existingUrls = new HashSet<>();
            existingUrls.add("http://existing.com");

            UUID articleId1 = UUID.randomUUID();
            UUID articleId2 = UUID.randomUUID();

            ArticleBackupDto dto1 = createArticleBackupDto(articleId1, "제목1", "http://url1.com");
            ArticleBackupDto dto2 = createArticleBackupDto(articleId2, "제목2", "http://url2.com");

            List<ArticleBackupDto> backupDtos = List.of(dto1, dto2);

            ResponseInputStream<GetObjectResponse> responseStream = mock(ResponseInputStream.class);
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);
            when(objectMapper.readValue(eq(responseStream), any(TypeReference.class))).thenReturn(backupDtos);

            // 2개의 행이 영향을 받았다고 가정
            when(jdbcTemplate.batchUpdate(anyString(), any(BatchPreparedStatementSetter.class)))
                .thenReturn(new int[]{1, 1});

            ArticleRestoreService.ProcessFileResult result = restoreService.processBackupFileWithTransaction(s3Object, existingUrls);

            assertThat(result).isNotNull();
            assertThat(result.success).isTrue();

            verify(s3Client).getObject(any(GetObjectRequest.class));
            verify(objectMapper).readValue(any(ResponseInputStream.class), any(TypeReference.class));
            verify(jdbcTemplate).batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));
        }

        @Test
        @DisplayName("백업 파일 복구 - 이미 존재하는 URL의 기사 스킵")
        void processBackupFileWithTransaction_SkipExistingUrls() throws Exception {
            S3Object s3Object = createS3Object("articles/2025-05-07/batch_uuid.json");

            Set<String> existingUrls = new HashSet<>();
            existingUrls.add("http://url1.com"); // 이미 존재하는 URL

            UUID articleId1 = UUID.randomUUID();
            UUID articleId2 = UUID.randomUUID();

            ArticleBackupDto dto1 = createArticleBackupDto(articleId1, "제목1", "http://url1.com"); // 이미 존재하는 URL
            ArticleBackupDto dto2 = createArticleBackupDto(articleId2, "제목2", "http://url2.com");

            List<ArticleBackupDto> backupDtos = List.of(dto1, dto2);

            ResponseInputStream<GetObjectResponse> responseStream = mock(ResponseInputStream.class);
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);
            when(objectMapper.readValue(eq(responseStream), any(TypeReference.class))).thenReturn(backupDtos);

            // 한 행만 영향을 받았다고 가정
            when(jdbcTemplate.batchUpdate(anyString(), any(BatchPreparedStatementSetter.class)))
                .thenReturn(new int[]{1});

            ArticleRestoreService.ProcessFileResult result = restoreService.processBackupFileWithTransaction(s3Object, existingUrls);

            assertThat(result).isNotNull();
            assertThat(result.success).isTrue();

            // 중복 URL 넘어갔는지 확인
            verify(s3Client).getObject(any(GetObjectRequest.class));
            verify(objectMapper).readValue(any(ResponseInputStream.class), any(TypeReference.class));
            verify(jdbcTemplate).batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));
        }

        @Test
        @DisplayName("백업 파일 복구 - 실패: JSON 파싱 오류 시 빈 결과를 반환")
        void processBackupFile_JsonParseError() throws Exception {
            S3Object s3Object = createS3Object("articles/2025-05-07/batch_uuid.json");
            Set<String> existingUrls = new HashSet<>();

            ResponseInputStream<GetObjectResponse> responseStream = mock(ResponseInputStream.class);
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);
            when(objectMapper.readValue(any(ResponseInputStream.class), any(TypeReference.class)))
                .thenThrow(new IOException("JSON 파싱 오류"));

            ArticleRestoreService.ProcessFileResult result = restoreService.processBackupFileWithTransaction(s3Object, existingUrls);

            assertThat(result.success).isTrue();
            assertThat(result.savedIds).isEmpty();
            assertThat(result.processedUrls).isEmpty();

            verify(s3Client).getObject(any(GetObjectRequest.class));
            verify(objectMapper).readValue(any(ResponseInputStream.class), any(TypeReference.class));
            verify(jdbcTemplate, never()).batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));
        }
        @Test
        @DisplayName("백업 파일 복구 - 실패: DB 오류 발생 시 빈 결과 반환")
        void processBackupFileWithTransaction_DatabaseError() throws Exception {
            S3Object s3Object = createS3Object("articles/2025-05-07/batch_uuid.json");
            Set<String> existingUrls = new HashSet<>();

            UUID articleId = UUID.randomUUID();
            ArticleBackupDto dto = createArticleBackupDto(articleId, "제목", "http://url.com");
            List<ArticleBackupDto> backupDtos = List.of(dto);

            ResponseInputStream<GetObjectResponse> responseStream = mock(ResponseInputStream.class);
            when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);
            when(objectMapper.readValue(any(ResponseInputStream.class), any(TypeReference.class))).thenReturn(backupDtos);

            when(jdbcTemplate.batchUpdate(anyString(), any(BatchPreparedStatementSetter.class)))
                .thenThrow(new RuntimeException("DB 오류"));

            ArticleRestoreService.ProcessFileResult result = restoreService.processBackupFileWithTransaction(s3Object, existingUrls);

            assertThat(result.success).isFalse();
            assertThat(result.savedIds).isEmpty();
            assertThat(result.processedUrls).isEmpty();

            verify(jdbcTemplate).batchUpdate(anyString(), any(BatchPreparedStatementSetter.class));
        }

    }

    @Nested
    @DisplayName("트랜잭션 동작 검증 테스트")
    class TransactionBoundaryTests {

        @Test
        @DisplayName("트랜잭션 동작 검증 - 백업 파일 처리 독립적으로 실행")
        void processFileWithNewTransaction_TransactionBoundary() {
            LocalDate date = LocalDate.of(2025, 5, 1);

            S3Object s3Object1 = createS3Object("articles/2025-05-07/batch_1.json");
            S3Object s3Object2 = createS3Object("articles/2025-05-07/batch_2.json");

            List<S3Object> s3Objects = List.of(s3Object1, s3Object2);

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(s3Objects)
                .build();

            when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(response);

            // 첫 번째 파일: 성공, 두 번째 파일: 실패
            UUID id1 = UUID.randomUUID();
            ArticleRestoreService.ProcessFileResult successResult =
                new ArticleRestoreService.ProcessFileResult(true, List.of(id1), Set.of("http://url1.com"));

            when(transactionService.processFileWithNewTransaction(any(), any(S3Object.class), anySet()))
                .thenReturn(successResult)
                .thenThrow(new RuntimeException("파일 처리 오류"));

            ArticleRestoreResultDto result = restoreService.restoreArticlesByDate(date);

            assertThat(result.restoredArticleIds()).hasSize(1);
            assertThat(result.restoredArticleIds()).contains(id1);

            verify(transactionService, times(2)).processFileWithNewTransaction(any(), s3ObjectCaptor.capture(), anySet());

            List<S3Object> capturedObjects = s3ObjectCaptor.getAllValues();
            assertThat(capturedObjects).hasSize(2);
            assertThat(capturedObjects.get(0).key()).isEqualTo("articles/2025-05-07/batch_1.json");
            assertThat(capturedObjects.get(1).key()).isEqualTo("articles/2025-05-07/batch_2.json");
        }
        @Test
        @DisplayName("트랜잭션 동작 검증 - 여러 파일 중 일부 실패 시 성공한 파일 결과만 반환")
        void multipleFiles_PartialFailure_TransactionIsolation() {
            LocalDate date = LocalDate.of(2025, 5, 7);

            S3Object successFile = createS3Object("articles/2025-05-07/batch_1.json");
            S3Object failureFile = createS3Object("articles/2025-05-07/batch_2.json");

            ListObjectsV2Response response = ListObjectsV2Response.builder()
                .contents(List.of(successFile, failureFile))
                .build();

            when(s3Client.listObjectsV2(any(ListObjectsV2Request.class)))
                .thenReturn(response);

            UUID articleId = UUID.randomUUID();
            ArticleRestoreService.ProcessFileResult successResult =
                new ArticleRestoreService.ProcessFileResult(true, List.of(articleId), Set.of("http://url1.com"));

            // 첫 번째 파일: 성공, 두 번째 파일: 실패
            when(transactionService.processFileWithNewTransaction(any(), eq(successFile), anySet()))
                .thenReturn(successResult);
            when(transactionService.processFileWithNewTransaction(any(), eq(failureFile), anySet()))
                .thenThrow(new ArticleBackupException(ArticleBackupErrorCode.S3_ACCESS_ERROR));

            ArticleRestoreResultDto result = restoreService.restoreArticlesByDate(date);

            assertThat(result).isNotNull();
            assertThat(result.restoredArticleIds()).hasSize(1);
            assertThat(result.restoredArticleIds()).contains(articleId);
            assertThat(result.restoredArticleCount()).isEqualTo(1L);

            // 두 트랜잭션 모두 처리되었는지 검증
            verify(transactionService).processFileWithNewTransaction(any(), eq(successFile), anySet());
            verify(transactionService).processFileWithNewTransaction(any(), eq(failureFile), anySet());
        }

    }
}
