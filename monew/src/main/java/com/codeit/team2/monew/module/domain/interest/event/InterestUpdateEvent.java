package com.codeit.team2.monew.module.domain.interest.event;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import java.util.List;
import java.util.UUID;

public record InterestUpdateEvent(
    Interest interest,
    List<String> keywords,
    UUID userId
) {

}
