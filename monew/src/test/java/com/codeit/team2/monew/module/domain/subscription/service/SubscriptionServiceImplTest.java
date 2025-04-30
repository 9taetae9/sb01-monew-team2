package com.codeit.team2.monew.module.domain.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.interest.TestInterestFactory;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.subscription.TestSubscriptionFactory;
import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.subscription.exception.DuplicateSubscriptionException;
import com.codeit.team2.monew.module.domain.subscription.exception.SubscriptionNotFoundException;
import com.codeit.team2.monew.module.domain.subscription.mapper.SubscriptionMapper;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.TestUserFactory;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private SubscriptionMapper subscriptionMapper = Mappers.getMapper(SubscriptionMapper.class);

    @Spy
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @DisplayName("유저가 관심사를 구독한다.")
    @Test
    void subscription_success() {
        // given
        User mockUser = TestUserFactory.createWithName("name");

        String name = "채소";
        List<String> inputKeywords = List.of("당근", "시금치");
        Interest mockInterest = TestInterestFactory.create(name, inputKeywords);

        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(mockUser));
        when(interestRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(mockInterest));
        when(subscriptionRepository.existsByInterestAndUser(mockInterest, mockUser))
            .thenReturn(false);

        // when
        SubscriptionDto result = subscriptionService.subscription(mockInterest.getId(),
            mockUser.getId());

        // then
        assertThat(result.interestKeywords()).hasSize(2).contains("당근", "시금치");
        assertThat(result.subscriberCount()).isEqualTo(1);

    }

    @DisplayName("유저가 관심사를 이미 구독중인 경우 실패한다.")
    @Test
    void subscription_failure() {
        // given
        User mockUser = TestUserFactory.createWithName("name");

        String name = "채소";
        List<String> inputKeywords = List.of("당근", "시금치");
        Interest mockInterest = TestInterestFactory.create(name, inputKeywords);

        Subscription mockSubscription = TestSubscriptionFactory.create(mockUser, mockInterest);

        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(mockUser));
        when(interestRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(mockInterest));
        when(subscriptionRepository.existsByInterestAndUser(mockInterest, mockUser))
            .thenReturn(true);

        // when & then
        assertThatThrownBy(
            () -> subscriptionService.subscription(mockInterest.getId(), mockSubscription.getId()))
            .isInstanceOf(DuplicateSubscriptionException.class);
    }

    @DisplayName("유저가 관심사 구독을 취소한다.")
    @Test
    void cancelSubscription_success() {
        // given
        User user = TestUserFactory.createWithName("hello");
        Interest interest = TestInterestFactory.create("채소", List.of("당근"));
        Subscription subscription = TestSubscriptionFactory.create(user, interest);

        // 구독된 경우
        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(user));
        when(interestRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(interest));
        when(subscriptionRepository.findByInterestAndUser(interest, user))
            .thenReturn(Optional.of(subscription));

        // when
        subscriptionService.cancelSubscription(interest.getId(), user.getId());

        // then 삭제 조회가 호출 되었는지
        verify(subscriptionRepository).deleteByInterestAndUser(interest, user);
    }

    @DisplayName("유저가 관심사 구독을 하지 않은 상태에서 취소하면 실패한다.")
    @Test
    void cancelSubscription_failure() {
        // given
        User user = TestUserFactory.createWithName("hello");
        Interest interest = TestInterestFactory.create("채소", List.of("당근"));
        Subscription subscription = TestSubscriptionFactory.create(user, interest);

        // 구독된 경우
        when(userRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(user));
        when(interestRepository.findById(any(UUID.class)))
            .thenReturn(Optional.of(interest));
        when(subscriptionRepository.findByInterestAndUser(interest, user))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
            () -> subscriptionService.cancelSubscription(interest.getId(), user.getId()))
            .isInstanceOf(SubscriptionNotFoundException.class);
    }

}
