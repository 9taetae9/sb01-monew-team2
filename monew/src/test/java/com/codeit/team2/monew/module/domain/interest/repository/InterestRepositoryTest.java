package com.codeit.team2.monew.module.domain.interest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@DataJpaTest
@Testcontainers
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InterestRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("monew")
        .withUsername("test")
        .withPassword("testpw")
        .withInitScript("init_pg_trgm.sql");

    @DynamicPropertySource
    static void setProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect",
            () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Autowired
    InterestRepository interestRepository;

    @DisplayName("관심사 이름 비교: 유사도 0.6, word_similarity 비교 성공")
    @Test
    void existsByNameSimilarTo_success() {
        // given
        String name = "programming";
        interestRepository.save(Interest.create(name));
        interestRepository.flush();

        // when
        String input = "programmi";
        boolean result = interestRepository.existsByNameSimilarTo(input);
        Double sim = interestRepository.getSimilarity(name, input);
        log.debug("유사도 = {}", sim);

        // then
        assertThat(result).isEqualTo(true);
    }

    @DisplayName("관심사 이름 비교: 유사도 0.6, word_similarity, 유사 비교 실패")
    @Test
    void existsByNameSimilarTo_failure() {
        // given
        String name = "programming";
        interestRepository.save(Interest.create(name));
        interestRepository.flush();

        // when
        String input = "progrannnn";
        boolean result = interestRepository.existsByNameSimilarTo(input);
        Double sim = interestRepository.getSimilarity(name, input);
        log.debug("유사도 = {}", sim);

        // then
        assertThat(result).isEqualTo(false);
    }
}
