package com.codeit.team2.monew.module.domain.article.backup.batch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleBackupDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
}
