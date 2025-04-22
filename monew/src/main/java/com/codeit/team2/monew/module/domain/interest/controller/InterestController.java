package com.codeit.team2.monew.module.domain.interest.controller;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.service.InterestService;
import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
import com.codeit.team2.monew.module.domain.subscription.service.SubscriptionService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

    private final InterestService interestService;
    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<InterestDto> create(
        @RequestHeader(name = "Monew-Request-User-ID") UUID userId,
        @RequestBody InterestRegisterRequest request
    ) {
        log.info("Start - InterestController/create: interest name={}", request.name());
        InterestDto interestDto = interestService.create(request, userId);
        log.info("Complete - InterestController/create: interest name={}", request.name());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(interestDto);
    }

    @PatchMapping("/{interestId}")
    public ResponseEntity<InterestDto> update(
        @RequestHeader(name = "Monew-Request-User-ID") UUID userId,
        @PathVariable(name = "interestId") UUID interestId,
        @RequestBody InterestUpdateRequest request
    ) {
        log.info("Start - InterestController/update: interest id={}", interestId);
        InterestDto interestDto = interestService.update(request, interestId, userId);
        log.info("Complete - InterestController/update: interest id={}", interestId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(interestDto);
    }

    @PostMapping("/{interestId}/subscriptions")
    public ResponseEntity<SubscriptionDto> subscription(
        @RequestHeader(name = "Monew-Request-User-ID") UUID userId,
        @PathVariable(name = "interestId") UUID interestId
    ) {
        log.info("Start - InterestController/subscriptione: interest id={}, userId={}", interestId, userId);
        SubscriptionDto subscriptionDto = subscriptionService.subscription(interestId, userId);
        log.info("Complete - InterestController/subscription: interest id={}, userId={}", interestId, userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(subscriptionDto);
    }
}
