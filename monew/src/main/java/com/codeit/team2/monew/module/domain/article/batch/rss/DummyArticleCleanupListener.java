package com.codeit.team2.monew.module.domain.article.batch.rss;

import com.codeit.team2.monew.module.domain.article.repository.DummyArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyArticleCleanupListener implements StepExecutionListener {

    private final DummyArticleRepository dummyArticleRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        jdbcTemplate.update("TRUNCATE TABLE dummy_articles RESTART IDENTITY;");
        return null;
    }
}
