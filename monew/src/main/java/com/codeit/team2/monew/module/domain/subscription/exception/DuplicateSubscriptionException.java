package com.codeit.team2.monew.module.domain.subscription.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;
import java.util.UUID;

public class DuplicateSubscriptionException extends BaseException {

    public DuplicateSubscriptionException(UUID interestId, UUID userId) {
        super(SubscriptionErrorCode.DUPLICATE_SUBSCRIPTION,
            Map.of("interestId", interestId, "userId", userId));
    }
}
