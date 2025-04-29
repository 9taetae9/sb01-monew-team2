package com.codeit.team2.monew.module.domain.subscription.service;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.subscription.event.SubscriptionDeleteEvent;
import com.codeit.team2.monew.module.domain.subscription.event.SubscriptionRegisterEvent;
import com.codeit.team2.monew.module.domain.subscription.mapper.SubscriptionMapper;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final InterestRepository interestRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public SubscriptionDto subscription(UUID interestId, UUID userId) {

        User user = getUserOrThrow(userId);
        Interest interest = getInterestOrThrow(interestId);

        if (subscriptionRepository.existsByInterestAndUser(interest, user)) {
            throw new DuplicateRequestException("이미 관심사를 구독하고 있습니다.");
        }

        Subscription subscription = interest.addSubscriber(user);
        Subscription savedSubscription = subscriptionRepository.save(subscription);

        List<String> keywords = interest.getKeywords().stream()
            .map(keyword -> keyword.getKeyword().getName())
            .collect(Collectors.toList());

        // 구독 생성 이벤트 발생
        publisher.publishEvent(new SubscriptionRegisterEvent(
            savedSubscription,
            interest,
            userId
        ));

        return subscriptionMapper.toDto(savedSubscription, interest, keywords);
    }

    @Override
    @Transactional
    public void cancelSubscription(UUID interestId, UUID userId) {

        User user = getUserOrThrow(userId);
        Interest interest = getInterestOrThrow(interestId);

        Subscription subscription = subscriptionRepository.findByInterestAndUser(interest, user)
            .orElseThrow(() -> new IllegalArgumentException("subscription not found"));

        // 구독 취소 이벤트 발생
        publisher.publishEvent(new SubscriptionDeleteEvent(
            subscription,
            userId
        ));

        subscriptionRepository.delete(subscription);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("user not found"));
    }

    private Interest getInterestOrThrow(UUID interestId) {
        return interestRepository.findById(interestId).orElseThrow(
            () -> new IllegalArgumentException("interest not found"));
    }
}
