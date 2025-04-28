package com.codeit.team2.monew.module.domain.interest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder
@Schema(description = "새로운 관심사 등록 요청 정보")
public record InterestRegisterRequest(

    @Schema(description = "관심사 이름", maxLength = 50, minLength = 1)
    @NotBlank(message = "관심사명을 비워둘 수 없습니다.")
    String name,

    @Schema(description = "관련 키워드 목록")
    @NotEmpty(message = "1개 이상의 키워드를 입력해야 합니다.")
    @Size(min = 1, max = 10)
    List<String> keywords

) {}
