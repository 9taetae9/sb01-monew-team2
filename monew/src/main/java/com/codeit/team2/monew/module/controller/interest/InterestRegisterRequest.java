package com.codeit.team2.monew.module.controller.interest;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

//@Builder(access = AccessLevel.PRIVATE)
public record InterestRegisterRequest(
    String name,
    List<String> keywords
) {

}
