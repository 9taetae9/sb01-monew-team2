package com.codeit.team2.monew.module.domain.article.batch.rss;


import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.article.repository.DummyArticleRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.repository.InterestKeywordRepository;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@SpringBatchTest
@Testcontainers
@Tag("integration")
@ActiveProfiles("test-postgre")
public class RssArticleBatchJobIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private DummyArticleRepository dummyArticleRepository;
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    private InterestRepository interestRepository;
    @Autowired
    private InterestKeywordRepository interestKeywordRepository;

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    @Qualifier("rssArticleBatchJob")
    private Job rssArticleBatchJob;

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
        registry.add("spring.batch.jdbc.initialize-schema", () -> "always");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.sql.init.mode", () -> "never");
        registry.add("spring.jpa.properties.hibernate.dialect",
            () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @BeforeAll
    static void startContainer() {
        postgres.start();
    }

    @Test
    @Sql(scripts = {"/schema-batch-postgres.sql"})
    void rssBatchJob_shouldProcessDummyArticlesAndSaveToArticleRepository_AndSaveToArticleInterestes()
        throws Exception {
        // given

        DummyArticle dummy = new DummyArticle(null, "AI", "NAVER", "http://AI.com",
            "AI AI AI", 0L, Instant.now(), false);
        dummyArticleRepository.save(dummy);

        Keyword keyword = new Keyword("AI");
        Interest interest = Interest.create("AI 관심사");
        InterestKeyword ik = new InterestKeyword(interest, keyword);

        keywordRepository.save(keyword);
        interestRepository.save(interest);
        interestKeywordRepository.save(ik);

        //when
        jobLauncherTestUtils.setJob(rssArticleBatchJob);
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        List<Article> articles = articleRepository.findAll();
        Assertions.assertThat(articles).hasSize(1);

        List<DummyArticle> dummys = dummyArticleRepository.findAll();
        Assertions.assertThat(dummys).isEmpty();
    }
}
