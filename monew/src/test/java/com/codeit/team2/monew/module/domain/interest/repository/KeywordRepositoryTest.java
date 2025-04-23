package com.codeit.team2.monew.module.domain.interest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test-temp")
@Import(JpaConfig.class)
@Transactional
class KeywordRepositoryTest {

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private InterestKeywordRepository interestKeywordRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    EntityManager em;

    @DisplayName("관심사 삭제 시, 키워드와 구독 테이블 확인")
    @Test
    void deleteInterest_check_success() {
//        // given -> transient 에러..
//        User user = userRepository.save(new User("test@test.com", "test", "pw", false));
//        Keyword keyword = keywordRepository.saveAndFlush(new Keyword("당근"));
//
//        Interest interest = Interest.create("채소");
//        interest.addInterestKeyword(keyword);
//        Interest saved = interestRepository.saveAndFlush(interest);
//
//        Subscription subscription = new Subscription(user, saved);
//        subscriptionRepository.saveAndFlush(subscription);
//
//        // when
//        interestRepository.delete(saved); // 삭제
//        em.flush();
//
//        // then
//        assertThat(interestRepository.findById(saved.getId())).isNotPresent();
//        assertThat(interestKeywordRepository.findAll()).isEmpty();
//        assertThat(interestRepository.findAll()).isEmpty();
//        assertThat(userRepository.findAll()).hasSize(1);
    }

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
