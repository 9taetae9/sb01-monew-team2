package com.codeit.team2.monew.module.domain.useractivity.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SubscriptionItemDto(
    UUID id,
    UUID interestId,
    String interestName,
    List<String> interestKeywords,
    Long interestSubscriberCount,
    Instant createdAt
) {

}
