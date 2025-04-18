package com.codeit.team2.monew.module.domain.interest.dto;

import java.util.List;
import java.util.UUID;

public record InterestDto(
    UUID id,
    String name,
    List<String> keywords,
    int subscriberCount,
    boolean subscribedByMe
) {

}
