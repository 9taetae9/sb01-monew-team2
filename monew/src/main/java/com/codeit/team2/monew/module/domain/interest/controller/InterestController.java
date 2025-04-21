package com.codeit.team2.monew.module.domain.interest.controller;

import com.codeit.team2.monew.module.domain.interest.dto.InterestDto;
import com.codeit.team2.monew.module.domain.interest.service.InterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

    private final InterestService interestService;

    @PostMapping
    public ResponseEntity<InterestDto> create(
        @RequestHeader(name = "Monew-Request-User-ID") String userId,
        @RequestBody InterestRegisterRequest request
    ) {
        InterestDto interestDto = interestService.create(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(interestDto);
    }
}
