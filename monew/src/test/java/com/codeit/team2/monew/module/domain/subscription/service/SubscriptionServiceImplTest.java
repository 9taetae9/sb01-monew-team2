package com.codeit.team2.monew.module.domain.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @DisplayName("유저가 관심사를 구독한다.")
    @Test
    void subscription_success() {
        // given
        User mockUser = new User("email@mail.com", "name", "pw", false);
        ReflectionTestUtils.setField(mockUser, "id", UUID.randomUUID());

        String name = "채소";
        List<String> inputKeywords = List.of("당근", "시금치");
        Interest mockInterest = createInterest(name, inputKeywords);
        ReflectionTestUtils.setField(mockInterest, "id", UUID.randomUUID());

        Subscription mockSubscription = new Subscription(mockUser, mockInterest);
        ReflectionTestUtils.setField(mockSubscription, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(mockSubscription, "createdAt", Instant.now());

        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(mockUser));
        when(interestRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(mockInterest));
        when(subscriptionRepository.existsByInterestAndUser(mockInterest, mockUser))
            .thenReturn(false);
        when(subscriptionRepository.save(any(Subscription.class)))
            .thenReturn(mockSubscription);

        // when
        SubscriptionDto result = subscriptionService.subscription(mockInterest.getId(), mockSubscription.getId());

        // then
        assertThat(result.interestKeywords()).hasSize(2).contains("당근", "시금치");
        assertThat(result.subscriberCount()).isEqualTo(1);
    }

    @DisplayName("유저가 관심사를 이미 구독중인 경우 실패한다.")
    @Test
    void subscription_failure() {
        // given
        User mockUser = new User("email@mail.com", "name", "pw", false);
        ReflectionTestUtils.setField(mockUser, "id", UUID.randomUUID());

        String name = "채소";
        List<String> inputKeywords = List.of("당근", "시금치");
        Interest mockInterest = createInterest(name, inputKeywords);
        ReflectionTestUtils.setField(mockInterest, "id", UUID.randomUUID());

        Subscription mockSubscription = new Subscription(mockUser, mockInterest);
        ReflectionTestUtils.setField(mockSubscription, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(mockSubscription, "createdAt", Instant.now());

        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(mockUser));
        when(interestRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(mockInterest));
        when(subscriptionRepository.existsByInterestAndUser(mockInterest, mockUser))
            .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> subscriptionService.subscription(mockInterest.getId(), mockSubscription.getId()))
            .isInstanceOf(DuplicateRequestException.class);
    }

    Interest createInterest(String name, List<String> keywords) {
        Interest mockInterest = Interest.create(name);
        for (String keyword : keywords) {
            mockInterest.addInterestKeyword(new Keyword(keyword));
        }
        return mockInterest;
    }

}
