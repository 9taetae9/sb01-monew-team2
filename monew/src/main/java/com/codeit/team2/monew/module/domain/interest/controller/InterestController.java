package com.codeit.team2.monew.module.domain.interest.controller;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.service.InterestService;
import com.codeit.team2.monew.module.domain.subscription.dto.SubscriptionDto;
import com.codeit.team2.monew.module.domain.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
        @Valid @RequestBody InterestRegisterRequest request
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
        @PathVariable(name = "interestId") UUID id,
        @Valid @RequestBody InterestUpdateRequest request
    ) {
        log.info("Start - InterestController/update: interest id={}", id);
        InterestDto interestDto = interestService.update(request, id, userId);
        log.info("Complete - InterestController/update: interest id={}", id);

        return ResponseEntity.status(HttpStatus.OK)
            .body(interestDto);
    }

    @PostMapping("/{interestId}/subscriptions")
    public ResponseEntity<SubscriptionDto> subscription(
        @RequestHeader(name = "Monew-Request-User-ID") UUID userId,
        @PathVariable(name = "interestId") UUID id
    ) {
        log.info("Start - InterestController/subscriptione: interest id={}, userId={}", id, userId);
        SubscriptionDto subscriptionDto = subscriptionService.subscription(id, userId);
        log.info("Complete - InterestController/subscription: interest id={}, userId={}", id, userId);

        return ResponseEntity.status(HttpStatus.OK)
            .body(subscriptionDto);
    }

    @DeleteMapping("/{interestId}")
    public ResponseEntity<Void> delete(
        @RequestHeader(name = "Monew-Request-User-ID") UUID userId,
        @PathVariable(name = "interestId") UUID id
    ) {
        log.info("Start - InterestController/subscriptione: interest id={}", id);
        interestService.delete(id, userId);
        log.info("Complete - InterestController/subscription: interest id={}", id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build();
    }

}
