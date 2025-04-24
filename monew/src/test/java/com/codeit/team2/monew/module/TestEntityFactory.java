package com.codeit.team2.monew.module;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class TestEntityFactory {

    public static DummyArticle dummyArticle(String title) {
        return new DummyArticle(
            UUID.randomUUID(),
            title,
            "TEST",
            "http://test.com",
            "Test Summary",
            0L,
            Instant.now(),
            false
        );
    }

    public static Article createArticle(String title) {
        Article article = new Article(
            title,
            "TEST",
            "http://test.com",
            "Test Summary",
            Set.of(),
            0L,
            Instant.now(),
            false
        );

        ReflectionTestUtils.setField(article, "id", UUID.randomUUID());
        return article;
    }

}
