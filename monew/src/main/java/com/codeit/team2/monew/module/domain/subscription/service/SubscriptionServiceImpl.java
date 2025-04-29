package com.codeit.team2.monew.module.domain.subscription.service;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.exception.InterestNotFoundException;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.subscription.event.SubscriptionRegisterEvent;
import com.codeit.team2.monew.module.domain.subscription.exception.DuplicateSubscriptionException;
import com.codeit.team2.monew.module.domain.subscription.exception.SubscriptionNotFoundException;
import com.codeit.team2.monew.module.domain.subscription.mapper.SubscriptionMapper;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
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
            throw new DuplicateSubscriptionException(interest.getId(), user.getId());
        }

        Subscription subscription = interest.addSubscriber(user);
        interestRepository.saveAndFlush(interest);

        List<String> keywords = interest.getKeywords().stream()
            .map(keyword -> keyword.getKeyword().getName())
            .collect(Collectors.toList());

        publisher.publishEvent(new SubscriptionRegisterEvent(
            subscription,
            interest,
            userId
        ));

        return subscriptionMapper.toDto(subscription, interest, keywords);
    }

    @Override
    public void cancelSubscription(UUID interestId, UUID userId) {

        User user = getUserOrThrow(userId);
        Interest interest = getInterestOrThrow(interestId);

        Subscription subscription = subscriptionRepository.findByInterestAndUser(interest, user)
            .orElseThrow(() -> new SubscriptionNotFoundException(interestId, userId));

        subscriptionRepository.delete(subscription);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("user not found"));
    }

    private Interest getInterestOrThrow(UUID interestId) {
        return interestRepository.findById(interestId).orElseThrow(
            () -> new InterestNotFoundException(interestId));
    }
}
