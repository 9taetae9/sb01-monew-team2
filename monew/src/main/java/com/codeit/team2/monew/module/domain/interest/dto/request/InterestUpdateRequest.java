package com.codeit.team2.monew.module.domain.interest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Schema(description = "수정할 관심사 정보")
@Builder
public record InterestUpdateRequest(

    @Schema(description = "수정 키워드 목록")
    @NotEmpty(message = "1개 이상의 키워드를 입력해야 합니다.")
    @Size(min = 1, max = 10)
    List<String> keywords

) {}
