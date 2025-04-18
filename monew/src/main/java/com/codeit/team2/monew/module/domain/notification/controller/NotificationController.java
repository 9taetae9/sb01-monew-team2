package com.codeit.team2.monew.module.domain.notification.controller;

import com.codeit.team2.monew.module.domain.notification.service.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> confirmNotification(
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        @PathVariable UUID notificationId) {
        notificationService.readNotification(userId, notificationId);
        return ResponseEntity.ok().build();
    }
}
