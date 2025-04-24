package com.codeit.team2.monew.module.domain.subscription;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.time.Instant;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class TestSubscriptionFactory {

    public static Subscription create(User user, Interest interest) {
        Subscription subscription = Subscription.create(user, interest);
        ReflectionTestUtils.setField(subscription, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(subscription, "createdAt", Instant.now());
        return subscription;
    }
}
