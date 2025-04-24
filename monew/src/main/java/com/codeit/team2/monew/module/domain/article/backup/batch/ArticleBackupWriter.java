package com.codeit.team2.monew.module.domain.article.backup.batch;

import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleBackupDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class ArticleBackupWriter implements ItemWriter<ArticleBackupDto> {

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("#{jobParameters['backupDate']}")
    private String backupDateStr;

    @Override
    public void write(Chunk<? extends ArticleBackupDto> items) throws Exception {
        if (items.isEmpty()) {
            log.info("No articles to backup");
            return;
        }

        LocalDate backupDate = LocalDate.parse(backupDateStr);
        String formattedDate = backupDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        String backupKey = String.format("articles/%s/batch_%s.json",
            formattedDate,
            java.util.UUID.randomUUID());

        String articlesJson = objectMapper.writeValueAsString(items.getItems());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(backupKey)
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(articlesJson));

        log.info("Successfully backed up {} articles to S3: {}", items.size(), backupKey);
    }
}
