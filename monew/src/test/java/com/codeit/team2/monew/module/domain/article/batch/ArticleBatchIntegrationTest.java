package com.codeit.team2.monew.module.domain.article.batch;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@SpringBatchTest
public class ArticleBatchIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
}
