package com.codeit.team2.monew.module.domain.article.batch.rss;


import static org.mockito.BDDMockito.given;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.repository.InterestKeywordRepository;
import com.codeit.team2.monew.module.domain.interest.repository.KeywordRepository;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class KeywordCacheTest {

    @Mock
    private KeywordRepository keywordRepository;
    @Mock
    private InterestKeywordRepository interestKeywordRepository;

    @InjectMocks
    private KeywordCache keywordCache;

    @Test
    @DisplayName("키워드 캐시를 정상적으로 만들 수 있다")
    void keywordCache_initialization_success() {
        // given
        Keyword k1 = new Keyword("test");
        Keyword k2 = new Keyword("test2");

        Interest i1 = Mockito.mock(Interest.class);
        Interest i2 = Mockito.mock(Interest.class);

        InterestKeyword ik1 = new InterestKeyword(i1, k1);
        InterestKeyword ik2 = new InterestKeyword(i1, k2);
        InterestKeyword ik3 = new InterestKeyword(i2, k1);

        given(keywordRepository.findAll())
            .willReturn(List.of(k1, k2));
//        given(interestKeywordRepository.findAllByKeyword(k1))
//            .willReturn(List.of(ik1, ik3));
//        given(interestKeywordRepository.findAllByKeyword(k2))
//            .willReturn(List.of(ik2));
        given(interestKeywordRepository.findAllWithInterests())
            .willReturn(List.of(ik1, ik2, ik3));
        // when
        keywordCache.refresh();
        Map<String, List<Interest>> cache = keywordCache.getCache();

        // then
        Assertions.assertThat(cache).hasSize(2);
        Assertions.assertThat(cache.get("test")).hasSize(2);

    }

}
