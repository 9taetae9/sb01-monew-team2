package com.codeit.team2.monew.module.domain.interest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.config.QuerydslConfig;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import jakarta.persistence.EntityManager;
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

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    EntityManager em;

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

    @DisplayName("연결된 InterestKeyword가 없는 고아 키워드만 조회한다")
    @Test
    void findOrphanKeywordsIn_success() {
        // given
        Keyword keyword1 = keywordRepository.save(new Keyword("감자"));
        Keyword keyword2 = keywordRepository.save(new Keyword("당근"));
        Keyword keyword3 = keywordRepository.save(new Keyword("고구마"));

        Interest interest = Interest.create("채소");
        interest.addKeyword(keyword1);
        interest.addKeyword(keyword2);
        interestRepository.saveAndFlush(interest);

        em.flush();
        em.clear();

        // when
        List<Keyword> orphanKeywords = keywordRepository.findOrphanKeywordsIn(
            List.of(keyword1, keyword2, keyword3));

        // then
        assertThat(orphanKeywords).hasSize(1);
        assertThat(orphanKeywords.get(0).getName()).isEqualTo("고구마");
    }

}
