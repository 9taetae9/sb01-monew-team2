package com.codeit.team2.monew.module.domain.notification.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;
import java.util.UUID;

public class NotificationNotFoundException extends BaseException {

    public NotificationNotFoundException(UUID notificationId) {
        super(NotificationErrorCode.NOTIFICATION_NOT_FOUND,
            Map.of("notificationId", notificationId));
    }
}
