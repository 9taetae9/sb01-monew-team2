package com.codeit.team2.monew.module.domain.article.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.NaverApiNewsClient;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@SpringBatchTest
@Testcontainers
@ActiveProfiles("test-postgre")
@Tag("integration")

public class ArticleBatchIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("monew")
        .withUsername("test")
        .withPassword("testpw");

    @DynamicPropertySource
    static void setProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect",
            () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    @Qualifier("articleBatchJob")
    private Job articleBatchJob;

    @Autowired
    private ArticleRepository articleRepository;
    @MockitoBean
    private NaverApiNewsClient naverNewsClient;


    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(articleBatchJob);

        Article mockArticle = new Article("mock", "NAVER", "http://mock.com", "mock", null, 0,
            Instant.now(), false);

        BDDMockito.given(naverNewsClient.fetchArticles(any(FetchCommand.class)))
            .willReturn(List.of(mockArticle));
    }


    @Test
    @Sql(scripts = {"/schema-batch-postgres.sql", "/insert-keywords.sql"})
    void testArticleBatchJob_shouldFetchArticleAndSave() throws Exception {
        // when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        assertThat(execution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");

        List<Article> articles = articleRepository.findAll();

        assertThat(articles).hasSize(1);
        assertThat(articles.get(0).getTitle()).isEqualTo("mock");
    }

}
