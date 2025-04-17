package com.codeit.team2.monew.module.domain.notification.dto;

import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    boolean confirmed,
    UUID userId,
    String content,
    ResourceType resourceType,
    UUID resourceId

) {

}
