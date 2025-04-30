package com.codeit.team2.monew.module.domain.subscription.Integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.module.domain.interest.TestInterestFactory;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.subscription.mapper.SubscriptionMapper;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.subscription.service.SubscriptionServiceImpl;
import com.codeit.team2.monew.module.domain.user.TestUserFactory;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled
@SpringBootTest
@Transactional
@ActiveProfiles({"test-temp"})
@Tag("integration")
public class SubscriptionServiceImplTest {

    // Could not resolve placeholder 'NAVER_CLIENT_ID' in value "${NAVER_CLIENT_ID}" 문제로 보류

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Autowired
    private SubscriptionServiceImpl subscriptionService;

    @DisplayName("유저가 관심사 구독을 취소한다.")
    @Test
    void cancelSubscription_success() {
        // given
        User user = TestUserFactory.createWithName("name");
        User savedUser = userRepository.saveAndFlush(user);

        Interest interest = TestInterestFactory.create("채소", List.of("당근", "시금치"));
        interest.addSubscriber(user);
        Interest savedInterest = interestRepository.saveAndFlush(interest);

        // when
        subscriptionService.cancelSubscription(savedInterest.getId(), savedUser.getId());

        // then
        assertThat(subscriptionRepository.existsByInterestAndUser(savedInterest, savedUser))
            .isEqualTo(false);
    }
}
