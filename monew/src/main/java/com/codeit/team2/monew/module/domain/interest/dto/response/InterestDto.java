package com.codeit.team2.monew.module.domain.interest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "관심사 정보")
public record InterestDto(

    @Schema(description = "관심사 ID", format = "uuid")
    UUID id,

    @Schema(description = "관심사 이름")
    String name,

    @Schema(description = "관련 키워드 목록")
    List<String> keywords,

    @Schema(description = "구독자 수", format = "int64")
    long subscriberCount,

    @Schema(description = "요청자의 구독 여부")
    boolean subscribedByMe

) {}
