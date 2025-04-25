package com.codeit.team2.monew.module.domain.useractivity.controller;

import com.codeit.team2.monew.module.domain.useractivity.controller.docs.UserActivityControllerDocs;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.service.UserActivityService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user-activities")
public class UserActivityController implements UserActivityControllerDocs {

    private final UserActivityService userActivityService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserActivityDto> findUserActivities(
        @RequestHeader("Monew-Request-User-ID") UUID loginId,
        @PathVariable UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userActivityService.findUserActivities(loginId, userId));
    }
}
