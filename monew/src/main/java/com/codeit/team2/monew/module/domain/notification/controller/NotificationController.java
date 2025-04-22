package com.codeit.team2.monew.module.domain.notification.controller;

import com.codeit.team2.monew.module.domain.notification.dto.CursorPageResponseNotificationDto;
import com.codeit.team2.monew.module.domain.notification.service.NotificationService;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
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

    @PatchMapping("")
    public ResponseEntity<Void> confirmAllNotifications(
        @RequestHeader("Monew-Request-User-ID") UUID userId) {
        notificationService.readAllNotifications(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public ResponseEntity<CursorPageResponseNotificationDto> findAll(
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        @RequestParam(required = false) Instant cursor,
        @RequestParam(required = false) Instant after,
        @RequestParam(required = true) int limit) {
        return ResponseEntity.ok(notificationService.findAll(userId, cursor, after, limit));
    }
}
