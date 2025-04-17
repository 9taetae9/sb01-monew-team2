package com.codeit.team2.monew.module.service;

import com.codeit.team2.monew.module.controller.interest.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.InterestDto;
import com.codeit.team2.monew.module.repository.interest.InterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;

    public InterestDto create(InterestRegisterRequest request) {


        return null;
    }

}
