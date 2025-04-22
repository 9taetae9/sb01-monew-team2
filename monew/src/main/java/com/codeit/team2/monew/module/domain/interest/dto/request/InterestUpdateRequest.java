package com.codeit.team2.monew.module.domain.interest.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

@Builder
public record InterestUpdateRequest(
    @NotEmpty(message = "1개 이상의 키워드를 입력해야 합니다.") List<String> keywords
) {

}
