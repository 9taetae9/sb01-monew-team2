package com.codeit.team2.monew.module.domain.interest.controller;

import java.util.List;
import lombok.Builder;

@Builder
public record InterestRegisterRequest(
    String name,
    List<String> keywords
) {

}
