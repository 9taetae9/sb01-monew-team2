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
import java.util.stream.Collectors;
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
        log.info("Creating Cache");

        cache = new HashMap<>();

        List<Keyword> keywords = keywordRepository.findAll();
        List<InterestKeyword> allInterestKeywords = interestKeywordRepository.findAllWithInterests();

        Map<String, List<Interest>> keywordToInterests = allInterestKeywords.stream()
            .filter(ik -> ik.getKeyword() != null && ik.getInterest() != null)
            .collect(
                Collectors.groupingBy(
                    ik -> ik.getKeyword().getName(),
                    Collectors.mapping(InterestKeyword::getInterest, Collectors.toList())
                )
            );

        for (Keyword keyword : keywords) {
            cache.put(keyword.getName(),
                keywordToInterests.getOrDefault(keyword.getName(), Collections.emptyList()));
        }
    }

    public Map<String, List<Interest>> getCache() {
        return Collections.unmodifiableMap(cache);
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroying Cache");
    }
}
