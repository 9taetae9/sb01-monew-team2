package com.codeit.team2.monew.module.domain.article.batch.rss;


import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@SpringBatchTest
@Testcontainers
@ActiveProfiles("test-postgre")
@Tag("integration")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RssArticleBatchIntegrationTestV2 {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("monew")
        .withUsername("test")
        .withPassword("testpw")
        .withInitScript("rss_init.sql");

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
    @Qualifier("rssArticleBatchJobV2")
    private Job rssArticleBatchJob;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private KeywordCache keywordCache;

    @Autowired
    @Qualifier("dummyArticleJdbcReader")
    private ItemReader<DummyArticle> dummyArticleReader;

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    static void startContainer() {
        postgres.start();
    }

    @BeforeEach
    void setup() throws Exception {
        jobLauncherTestUtils.setJob(rssArticleBatchJob);
    }


    @Test
//    @Sql(scripts = "/rss_init.sql")
    @Sql(scripts = "/schema-batch-postgres.sql")
    @Sql(scripts = "/rss-batch-integration-test-data.sql", config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
    void DummyArticle에서_읽어온_기사를_Article로_저장할_수_있다() throws Exception {

        // when
        keywordCache.refresh();
        Assertions.assertThat(articleRepository.findAll()).hasSize(0);
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        List<Article> articles = articleRepository.findAll();
        Assertions.assertThat(articles).hasSize(1);
    }
}
