package com.codeit.team2.monew.module.domain.article.batch.rss;


import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.repository.InterestKeywordRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeywordCache {

    private final KeywordRepository keywordRepository;
    private final InterestKeywordRepository interestKeywordRepository;
    private Map<String, List<Interest>> cache; // keyword to interest cache


    @Transactional
    public void refresh() {
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

    public String buildtoTsQuery() {
        return cache.keySet().stream()
            .map(k -> k.contains(" ") ? null : k + ":*")  // 띄어쓰기 있는 키워드는 제외
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" | "));
    }


}
