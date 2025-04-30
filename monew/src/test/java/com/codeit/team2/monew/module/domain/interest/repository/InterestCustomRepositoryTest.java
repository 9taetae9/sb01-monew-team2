package com.codeit.team2.monew.module.domain.interest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.config.QuerydslConfig;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestOrderBy;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test-temp")
@Import({JpaConfig.class, QuerydslConfig.class})
public class InterestCustomRepositoryTest {


    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private KeywordRepository keywordRepository;

    @Qualifier("interestCustomRepositoryImpl")
    @Autowired
    private InterestCustomRepository interestCustomRepository;

    @Autowired
    private TestEntityManager em;

    @Transactional
    @Test
    @DisplayName("관심사 목록 조회")
    void testFindAllNameASC() {
        // given
        // 관심사 데이터
        Interest i1 = Interest.create("i1");
        Interest i2 = Interest.create("i2");
        Interest i3 = Interest.create("i3");

        interestRepository.saveAll(List.of(i1, i2, i3));

        em.flush();
        em.clear();

        String keyword = null;
        InterestOrderBy orderBy = InterestOrderBy.name;
        Direction direction = Direction.ASC;
        String cursor = null;
        Instant after = Instant.now();
        int limit = 2;

        // when
        Slice<Interest> result = interestCustomRepository.findAll(keyword, orderBy, direction,
            cursor, after, limit);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent().get(0).getName()).isEqualTo("i1");
    }
}
