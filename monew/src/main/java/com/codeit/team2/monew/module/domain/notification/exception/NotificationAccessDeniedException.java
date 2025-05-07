package com.codeit.team2.monew.module.domain.notification.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;
import java.util.UUID;

public class NotificationAccessDeniedException extends BaseException {

    public NotificationAccessDeniedException(UUID userId, UUID notificationId) {
        super(NotificationErrorCode.NOTIFICATION_ACCESS_DENIED,
            Map.of("userId", userId, "notificationId", notificationId));
    }
}

