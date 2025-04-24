package com.codeit.team2.monew.module.domain.interest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test-temp")
@Import(JpaConfig.class)
public class InterestRepositoryTest {
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

    @DisplayName("관심사 삭제 시, 키워드와 구독 테이블 확인")
    @Test
    void deleteInterest_check_success() {
        // given
        User user = userRepository.save(new User("test@test.com", "test", "pw", false));
        Keyword keyword = keywordRepository.saveAndFlush(new Keyword("당근"));

        Interest interest = Interest.create("채소");
        interest.addKeyword(keyword);
        interest.addSubscriber(user);

        Interest savedInterest = interestRepository.saveAndFlush(interest);

        // when
        interestRepository.delete(savedInterest);

        // then
        assertThat(interestRepository.findById(savedInterest.getId())).isNotPresent();
        assertThat(interestKeywordRepository.findAll()).isEmpty();
        assertThat(subscriptionRepository.findAll()).isEmpty();
        assertThat(keywordRepository.findByName(keyword.getName())).isPresent();
        assertThat(userRepository.findById(user.getId())).isPresent();

    }

}
