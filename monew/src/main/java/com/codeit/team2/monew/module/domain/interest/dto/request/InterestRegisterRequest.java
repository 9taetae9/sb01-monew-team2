package com.codeit.team2.monew.module.domain.interest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record InterestRegisterRequest(
    @NotBlank(message = "관심사명을 비워둘 수 없습니다.") String name,
    @NotEmpty(message = "1개 이상의 키워드를 입력해야 합니다.") List<String> keywords
) {

}
