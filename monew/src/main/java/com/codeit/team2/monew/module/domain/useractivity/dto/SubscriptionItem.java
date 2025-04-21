package com.codeit.team2.monew.module.domain.useractivity.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubscriptionItem(
    UUID id,
    UUID interestId,
    String interestName,
    List<String> interestKeywords,
    Integer interestSubscriberCount,
    Instant createdAt
) {

}
