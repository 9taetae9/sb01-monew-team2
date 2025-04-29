package com.codeit.team2.monew.module.domain.interest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.config.QuerydslConfig;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test-temp")
@Import({JpaConfig.class, QuerydslConfig.class})
class KeywordRepositoryTest {

    @Autowired
    private KeywordRepository keywordRepository;

    @DisplayName("관심사에 사용되지 않는 키워드 벌크 삭제")
    @Test
    void deleteAllOrphanKeywords_success() {
        // given
        Keyword keyword1 = new Keyword("당근");
        Keyword keyword2 = new Keyword("시금치");

        keywordRepository.saveAll(List.of(keyword1, keyword2));

        // when
        keywordRepository.deleteAllOrphanKeywords();

        // then
        List<Keyword> result = keywordRepository.findAll();
        assertThat(result).hasSize(0);
    }

}
