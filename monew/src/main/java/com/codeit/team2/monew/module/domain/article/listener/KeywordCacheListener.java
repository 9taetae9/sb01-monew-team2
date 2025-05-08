package com.codeit.team2.monew.module.domain.article.listener;

import com.codeit.team2.monew.module.domain.article.batch.rss.KeywordCache;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class KeywordCacheListener implements ApplicationListener<ContextRefreshedEvent> {

    private final KeywordCache cache;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        cache.refresh();
    }
}
