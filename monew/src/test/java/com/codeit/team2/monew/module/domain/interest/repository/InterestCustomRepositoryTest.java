package com.codeit.team2.monew.module.domain.interest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.config.QuerydslConfig;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestOrderBy;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.time.Instant;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
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
@Transactional
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

    @BeforeEach
    void setUp() {
        User user1 = new User("email1", "nickname1", "password", false);
        User user2 = new User("email2", "nickname2", "password", false);
        User user3 = new User("email3", "nickname3", "password", false);

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        // 관심사 생성
        Interest i1 = Interest.create("i1");
        Interest i2 = Interest.create("i2");
        Interest i3 = Interest.create("i3");
        Interest i4 = Interest.create("i4");

        em.persist(i1);
        em.persist(i2);
        em.persist(i3);
        em.persist(i4);

        // i1: 2명, i2: 3명
        i1.addSubscriber(user1);
        i2.addSubscriber(user1);
        i2.addSubscriber(user2);
        i3.addSubscriber(user1);
        i3.addSubscriber(user2);
        i3.addSubscriber(user3);

        Keyword k1 = new Keyword("alpha");
        Keyword k2 = new Keyword("beta");

        i1.addKeyword(k1);  // i1 - alpha
        i2.addKeyword(k2);  // i2 - beta

        em.persist(k1);
        em.persist(k2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("관심사 목록 조회 - name ASC")
    void testFindAllNameASC() {
        String keyword = null;
        InterestOrderBy orderBy = InterestOrderBy.name;
        Direction direction = Direction.ASC;
        String cursor = null;
        Instant after = Instant.now();
        int limit = 2;

        Slice<Interest> result = interestCustomRepository.findAll(
            keyword, orderBy, direction, cursor, after, limit
        );

        assertThat(result).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent())
            .isSortedAccordingTo(Comparator.comparing(Interest::getName));
    }

    @Test
    @DisplayName("커서 기반 조회 - name ASC")
    void testFindAllNameASCWithCursor() {
        // 첫 페이지 가져오기
        Slice<Interest> slice = interestCustomRepository.findAll(
            null, InterestOrderBy.name, Direction.ASC, null, null, 2
        );

        assertThat(slice).hasSize(2);
        assertThat(slice.hasNext()).isTrue();

        // 다음 페이지 커서 조회
        String nextCursor = slice.getContent().get(1).getName();

        Slice<Interest> nextSlice = interestCustomRepository.findAll(
            null, InterestOrderBy.name, Direction.ASC, nextCursor, null, 2
        );

        assertThat(nextSlice).isNotEmpty();
    }

    @Test
    @DisplayName("커서 기반 조회 - subscriberCount DESC")
    void testFindAllSubscriberCountDESCWithCursor() {
        // 첫 페이지
        Slice<Interest> slice = interestCustomRepository.findAll(
            null, InterestOrderBy.subscriberCount, Direction.DESC, null, null, 2
        );

        assertThat(slice).hasSize(2);
        assertThat(slice.hasNext()).isTrue();

        // 다음 페이지 커서 조회
        Interest last = slice.getContent().get(1);
        String nextCursor = String.valueOf(last.getSubscriberCount());
        Instant after = last.getCreatedAt();

        Slice<Interest> nextSlice = interestCustomRepository.findAll(
            null, InterestOrderBy.subscriberCount, Direction.DESC, nextCursor, after, 2
        );

        assertThat(nextSlice).isNotEmpty();
    }

    @Test
    @DisplayName("검색 키워드 없이 전체 카운트")
    void testCountFilteredTotalElements_noKeyword() {
        long count = interestCustomRepository.countFilteredTotalElements(null, InterestOrderBy.name,
            Direction.ASC);

        assertThat(count).isEqualTo(4);
    }

    @Test
    @DisplayName("검색 키워드로 필터링된 카운트")
    void testCountFilteredTotalElements_withKeyword() {
        long count = interestCustomRepository.countFilteredTotalElements("alpha",
            InterestOrderBy.name, Direction.ASC);

        assertThat(count).isGreaterThan(0);
    }

    @Test
    @DisplayName("검색 키워드 포함 조회 - name ASC")
    void testFindAllWithKeyword() {
        String keyword = "i";  // i1, i2 등 포함하는 키워드
        InterestOrderBy orderBy = InterestOrderBy.name;
        Direction direction = Direction.ASC;
        int limit = 10;

        Slice<Interest> result = interestCustomRepository.findAll(
            keyword, orderBy, direction, null, null, limit
        );

        assertThat(result).isNotEmpty();
        assertThat(result.getContent())
            .allMatch(interest -> interest.getName().toLowerCase().contains("i"));
    }


}
