package com.codeit.team2.monew.module.domain.notification.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CursorPageResponseNotificationDto(
    List<NotificationDto> content,
    Object nextCursor,
    Instant nextAfter,
    int size,
    Long totalElements,
    boolean hasNext

) {

}
