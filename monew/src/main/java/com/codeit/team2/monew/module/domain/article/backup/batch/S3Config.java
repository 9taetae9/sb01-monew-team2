package com.codeit.team2.monew.module.domain.article.backup.batch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    @Value("${aws.profile-name:monew-acc}")
    private String awsProfile;

    @Value("${aws.region:ap-northeast-2}")
    private String awsRegion;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(ProfileCredentialsProvider.builder()
                .profileName(awsProfile)
                .build())
            .build();
    }
}
