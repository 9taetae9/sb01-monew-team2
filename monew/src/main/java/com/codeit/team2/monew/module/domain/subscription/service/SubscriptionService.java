package com.codeit.team2.monew.module.domain.subscription.service;

import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
import java.util.UUID;

public interface SubscriptionService {

    SubscriptionDto subscription(UUID interestId, UUID userId);

    void cancelSubscription(UUID id, UUID userId);
}
