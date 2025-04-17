package com.codeit.team2.monew.module.domain.interest.dto;

import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import java.util.List;
import java.util.UUID;

public record InterestDto(
    UUID id,
    String name,
    List<Keyword> keywords,
    int subscriberCount,
    boolean subscribedByMe
) {

}
