package com.codeit.team2.monew.module.domain.article.batch.rss;

import com.codeit.team2.monew.module.domain.article.repository.DummyArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DummyArticleCleanupListener implements StepExecutionListener {

    private final DummyArticleRepository dummyArticleRepository;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        dummyArticleRepository.deleteAll(); // TODO: For 문 도는것 같은데 개선 사항
        return null;
    }
}
