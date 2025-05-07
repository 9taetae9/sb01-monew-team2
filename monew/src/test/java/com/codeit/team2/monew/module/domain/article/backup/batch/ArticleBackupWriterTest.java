package com.codeit.team2.monew.module.domain.article.backup.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleBackupDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@ExtendWith(MockitoExtension.class)
class ArticleBackupWriterTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private ObjectMapper objectMapper;

    private ArticleBackupWriter writer;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;

    @Captor
    private ArgumentCaptor<RequestBody> requestBodyCaptor;

    @BeforeEach
    void setUp() {
        writer = new ArticleBackupWriter(s3Client, objectMapper);
        String bucketName = "test-bucket";
        ReflectionTestUtils.setField(writer, "bucketName", bucketName);
        String backupDate = "2025-05-07";
        ReflectionTestUtils.setField(writer, "backupDateStr", backupDate);
    }

    @Test
    @DisplayName("기사 백업 데이터 S3 저장 - 성공")
    void write_ShouldSuccessfullyWriteToS3() throws Exception {
        ArticleBackupDto dto1 = new ArticleBackupDto(
            UUID.randomUUID(),
            "제목1",
            "출처1",
            "http://url1.com",
            "요약1",
            10L,
            Instant.now(),
            false,
            Set.of(UUID.randomUUID()),
            Instant.now()
        );

        ArticleBackupDto dto2 = new ArticleBackupDto(
            UUID.randomUUID(),
            "제목2",
            "출처2",
            "http://url2.com",
            "요약2",
            20L,
            Instant.now(),
            false,
            Set.of(UUID.randomUUID()),
            Instant.now()
        );

        Chunk<ArticleBackupDto> chunk = new Chunk<>(List.of(dto1, dto2));
        String serializedJson = "[{...},{...}]";

        when(objectMapper.writeValueAsString(any())).thenReturn(serializedJson);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().build());

        assertDoesNotThrow(() -> writer.write(chunk));

        verify(objectMapper).writeValueAsString(chunk.getItems());
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("기사 백업 데이터 S3 저장 - 빈 청크는 S3에 저장하지 않고 종료")
    void write_ShouldHandleEmptyChunk() throws Exception {
        Chunk<ArticleBackupDto> emptyChunk = new Chunk<>();

        writer.write(emptyChunk);

        verify(objectMapper, never()).writeValueAsString(any());
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("다양한 크기의 백업 데이터 처리 검증")
    void write_ShouldHandleDifferentDataSizes() throws Exception {
        Chunk<ArticleBackupDto> smallBatch = new Chunk<>(createBackupDtos(5));
        Chunk<ArticleBackupDto> mediumBatch = new Chunk<>(createBackupDtos(50));
        Chunk<ArticleBackupDto> largeBatch = new Chunk<>(createBackupDtos(200));

        String smallJson = "{" + "\"item\":".repeat(5) + "}";
        String mediumJson = "{" + "\"item\":".repeat(50) + "}";
        String largeJson = "{" + "\"item\":".repeat(200) + "}";

        when(objectMapper.writeValueAsString(smallBatch.getItems())).thenReturn(smallJson);
        when(objectMapper.writeValueAsString(mediumBatch.getItems())).thenReturn(mediumJson);
        when(objectMapper.writeValueAsString(largeBatch.getItems())).thenReturn(largeJson);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().build());

        writer.write(smallBatch);
        writer.write(mediumBatch);
        writer.write(largeBatch);

        verify(objectMapper, times(3)).writeValueAsString(any());
        verify(s3Client, times(3)).putObject(putObjectRequestCaptor.capture(), requestBodyCaptor.capture());

        List<RequestBody> bodies = requestBodyCaptor.getAllValues();
        assertThat(bodies).hasSize(3);

        // 각 배치의 요청 content 크기가 증가하는지 확인
        assertThat(bodies.get(0).contentLength()).isPositive();
        assertThat(bodies.get(1).contentLength()).isGreaterThan(bodies.get(0).contentLength());
        assertThat(bodies.get(2).contentLength()).isGreaterThan(bodies.get(1).contentLength());

        // 실제 배치 크기가 RequestBody 크기와 비례관계인지 확인
        double ratio1 = (double) bodies.get(1).contentLength() / bodies.get(0).contentLength();
        double ratio2 = (double) bodies.get(2).contentLength() / bodies.get(1).contentLength();

        // 배치 크기 비율(50/5=10, 200/50=4)과 RequestBody 크기 비율이 유사한지 확인 (±10% 오차 허용)
        assertThat(ratio1).isBetween(9.0, 11.0); // 10 ±10%
        assertThat(ratio2).isBetween(3.6, 4.4);  // 4 ±10%
    }

    @Test
    @DisplayName("백업 저장 키 경로 검증")
    void write_ShouldRecordMetadata() throws Exception {
        List<ArticleBackupDto> backupDtos = createBackupDtos(10);
        Chunk<ArticleBackupDto> chunk = new Chunk<>(backupDtos);

        when(objectMapper.writeValueAsString(any())).thenReturn("json-content");
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().build());

        writer.write(chunk);

        verify(s3Client).putObject(putObjectRequestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest capturedRequest = putObjectRequestCaptor.getValue();

        // 키 경로 검증
        String expectedKeyPrefix = "articles/2025-05-07/batch_";
        assertThat(capturedRequest.key()).startsWith(expectedKeyPrefix);
        assertThat(capturedRequest.key()).endsWith(".json");

        // UUID 패턴 검증 - 키에 UUID가 포함되어 있는지 확인
        String key = capturedRequest.key();
        String uuidPart = key.substring(expectedKeyPrefix.length(), key.length() - 5);
        assertThat(uuidPart).matches("[a-f0-9\\-]{36}"); // UUID 형식 검증
    }

    @Test
    @DisplayName("기사 백업 데이터 S3 저장 - 실패: 예외 발생")
    void write_ShouldPropagateS3Exception() throws Exception {
        ArticleBackupDto dto = new ArticleBackupDto(
            UUID.randomUUID(),
            "제목",
            "출처",
            "http://url.com",
            "요약",
            10L,
            Instant.now(),
            false,
            Set.of(UUID.randomUUID()),
            Instant.now()
        );

        Chunk<ArticleBackupDto> chunk = new Chunk<>(List.of(dto));
        String serializedJson = "{...}";

        when(objectMapper.writeValueAsString(any())).thenReturn(serializedJson);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenThrow(new RuntimeException("S3 업로드 실패"));

        assertThrows(RuntimeException.class, () -> writer.write(chunk));

        verify(objectMapper).writeValueAsString(chunk.getItems());
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("기사 백업 데이터 S3 저장 - 실패: JSON 직렬화 실패")
    void write_ShouldPropagateJsonSerializationException() throws Exception {
        ArticleBackupDto dto = new ArticleBackupDto(
            UUID.randomUUID(),
            "제목",
            "출처",
            "http://url.com",
            "요약",
            10L,
            Instant.now(),
            false,
            Set.of(UUID.randomUUID()),
            Instant.now()
        );

        Chunk<ArticleBackupDto> chunk = new Chunk<>(List.of(dto));

        when(objectMapper.writeValueAsString(any()))
            .thenThrow(new RuntimeException("JSON 직렬화 실패"));

        assertThrows(RuntimeException.class, () -> writer.write(chunk));

        verify(objectMapper).writeValueAsString(chunk.getItems());
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    private List<ArticleBackupDto> createBackupDtos(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> new ArticleBackupDto(
                UUID.randomUUID(),
                "제목-" + i,
                "출처-" + i,
                "http://test-" + i + ".com",
                "요약-" + i,
                (long) i,
                Instant.now(),
                false,
                Set.of(UUID.randomUUID()),
                Instant.now()
            ))
            .collect(Collectors.toList());
    }
}
