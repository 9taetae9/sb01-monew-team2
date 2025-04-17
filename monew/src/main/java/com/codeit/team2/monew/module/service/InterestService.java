package com.codeit.team2.monew.module.service;

import com.codeit.team2.monew.module.controller.interest.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.InterestDto;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.mapper.interest.InterestMapper;
import com.codeit.team2.monew.module.repository.interest.InterestRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;

    public InterestDto create(InterestRegisterRequest request) {

        Interest interest = Interest.builder()
            .name(request.name())
            .keywords(new ArrayList<>())
            .subscriptions(new ArrayList<>())
            .subscriberCount(0)
            .build();

        return InterestMapper.INSTANCE.toDto(interest, List.of(request.keywords().get(0)) ,true);
    }

}
