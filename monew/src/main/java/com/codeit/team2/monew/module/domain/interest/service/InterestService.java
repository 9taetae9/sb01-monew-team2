package com.codeit.team2.monew.module.domain.interest.service;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestUpdateRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import java.util.UUID;

public interface InterestService {

    InterestDto create(InterestRegisterRequest request, UUID userId);

    InterestDto update(InterestUpdateRequest request, UUID id, UUID userId);

}
