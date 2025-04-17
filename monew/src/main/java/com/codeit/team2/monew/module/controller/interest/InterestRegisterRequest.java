package com.codeit.team2.monew.module.controller.interest;

import java.util.List;
import lombok.Builder;

@Builder
public record InterestRegisterRequest(
    String name,
    List<String> keywords
) {

}
