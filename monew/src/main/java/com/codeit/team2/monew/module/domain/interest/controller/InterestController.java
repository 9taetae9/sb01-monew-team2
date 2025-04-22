package com.codeit.team2.monew.module.domain.interest.controller;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.service.InterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<InterestDto> create(
        @Valid @RequestHeader(name = "Monew-Request-User-ID") String userId,
        @RequestBody InterestRegisterRequest request
    ) {
        log.info("Start - InterestController/create: interest name={}", request.name());
        InterestDto interestDto = interestService.create(request, userId);
        log.info("Complete - InterestController/create: interest name={}", request.name());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(interestDto);
    }
}
