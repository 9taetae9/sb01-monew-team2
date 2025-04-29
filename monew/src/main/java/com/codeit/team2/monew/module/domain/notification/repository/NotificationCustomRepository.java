package com.codeit.team2.monew.module.domain.notification.repository;

import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface NotificationCustomRepository {

    Slice<Notification> findWithCursor(UUID userId, Instant cursor, UUID after, int limit);
}
