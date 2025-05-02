package com.codeit.team2.monew.module.domain.subscription.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;
import java.util.UUID;

public class SubscriptionNotFoundException extends BaseException {

    public SubscriptionNotFoundException(UUID interestId, UUID userId) {
        super(SubscriptionErrorCode.SUBSCRIPTION_NOT_FOUND,
            Map.of("interestId", interestId, "userId", userId));
    }
}
