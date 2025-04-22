package com.codeit.team2.monew.module.domain.subscription.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubscriptionDto (
    UUID id,
    UUID interestId,
    String interestName,
    List<String> interestKeywords,
    int subscriberCount,
    Instant createdAt
) {

}
