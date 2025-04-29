package com.codeit.team2.monew.module.domain.interest.event;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import java.util.UUID;

public record InterestDeleteEvent(
    Interest interest,
    UUID userId
) {

}
