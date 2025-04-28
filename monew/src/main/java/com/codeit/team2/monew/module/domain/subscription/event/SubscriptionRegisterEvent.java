package com.codeit.team2.monew.module.domain.subscription.event;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import java.util.UUID;

public record SubscriptionRegisterEvent(
    Subscription subscription,
    Interest interest,
    UUID userId
) {

}
