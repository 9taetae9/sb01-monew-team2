package com.codeit.team2.monew.module.domain.article.batch.rss;


import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.repository.InterestKeywordRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class KeywordCache {

    private final KeywordRepository keywordRepository;
    private final InterestKeywordRepository interestKeywordRepository;
    private Map<String, List<Interest>> cache; // keyword to interest cache

    @PostConstruct
    public void init() {
        log.debug("Creating Cache");

        cache = new HashMap<>();

        List<Keyword> keywords = keywordRepository.findAll();

        // TODO: IN (keywords) 로 한번에 조회 고민
        for (Keyword keyword : keywords) {
            List<Interest> interests = interestKeywordRepository.findAllByKeyword(keyword).stream()
                .map(InterestKeyword::getInterest).toList();
            cache.put(keyword.getName(), interests);
        }
    }

    public Map<String, List<Interest>> getCache() {
        return Collections.unmodifiableMap(cache);
    }

    @PreDestroy
    public void destroy() {
        log.debug("Destroying Cache");
    }
}
