package com.codeit.team2.monew.module.log;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Tag("integration")
public class LogUploadServiceIntegrationTest {

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
        .withServices(S3);

    @TempDir
    Path tempDir;

    private Path logArchivePath;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ZoneId TIME_ZONE = ZoneId.of("Asia/Seoul");

    @Autowired
    private LogUploadService logUploadService;

    @Autowired
    private S3Client s3Client;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public S3Client s3Client() {
            return S3Client.builder()
                .endpointOverride(localStack.getEndpointOverride(S3))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
                .region(Region.of(localStack.getRegion()))
                .build();
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        logArchivePath = tempDir.resolve("archive");
        Files.createDirectories(logArchivePath);

        s3Client.createBucket(CreateBucketRequest.builder().bucket("test-bucket").build());

        ReflectionTestUtils.setField(logUploadService, "logArchivePath", logArchivePath.toString());
    }

    @Test
    void testUploadLogToS3_EndToEnd() throws IOException {
        LocalDate yesterday = LocalDate.now(TIME_ZONE).minusDays(1);
        String dateStr = yesterday.format(DATE_FORMAT);
        String logFileName = "application." + dateStr + ".log";
        Path logFile = logArchivePath.resolve(logFileName);

        Files.writeString(logFile, "This is a test log content for S3 upload integration test.");

        boolean result = logUploadService.uploadLogByDate(yesterday);

        assertTrue(result, "업로드가 성공해야 함");

        // S3에 파일이 존재하는지 확인
        String s3Key = "logs/" + dateStr + "/" + logFileName + ".gz";
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                .bucket("test-bucket")
                .key(s3Key)
                .build());

            // 예외가 발생하지 않을 경우 파일 존재
            assertTrue(true);
        } catch (Exception e) {
            fail("S3에 업로드된 파일이 존재해야 함: " + e.getMessage());
        }

        // 로컬 압축 파일이 삭제되었는지 확인
        assertFalse(Files.exists(logArchivePath.resolve(logFileName + ".gz")));
    }
}
