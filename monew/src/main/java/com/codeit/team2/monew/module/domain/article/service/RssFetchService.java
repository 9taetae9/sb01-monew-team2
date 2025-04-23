package com.codeit.team2.monew.module.domain.article.service;


import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.article.external.RssNewsClient;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RssFetchService {

    private final List<RssNewsClient> clients;
    private final JdbcTemplate jdbcTemplate;

    public void fetchAllRss() {

        log.debug("Start - fetchAllArticles: time={}", Instant.now());

        String sql = """
                INSERT INTO dummy_articles (id, title, source, source_url, summary, view_count, published_date, deleted)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (source_url) DO NOTHING
            """;

        try {
            for (RssNewsClient client : clients) {
                log.debug("Fetching articles: for={}", client.getClass().getSimpleName());

                List<DummyArticle> dummyArticles = client.fetchArticles();
                jdbcTemplate.batchUpdate(sql, dummyArticles, 1000, (ps, da) -> {
                    UUID id = UUID.randomUUID();
                    ps.setObject(1, id);
                    ps.setObject(2, da.getTitle());
                    ps.setObject(3, da.getSource());
                    ps.setObject(4, da.getSourceUrl());
                    ps.setObject(5, da.getSummary());
                    ps.setObject(6, da.getViewCount());
                    ps.setObject(7, Timestamp.from(da.getPublishedDate()));
                    ps.setObject(8, da.getDeleted());
                });
            }
        } catch (Exception e) {
            log.warn("ERROR: reason={}", e.getMessage());
        }
    }
}
