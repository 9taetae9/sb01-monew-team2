package com.codeit.team2.monew.module.domain.useractivity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "구독 정보 항목")
public record SubscriptionItemDto(

    @Schema(description = "구독 정보 ID", format = "uuid")
    UUID id,

    @Schema(description = "관심사 ID", format = "uuid")
    UUID interestId,

    @Schema(description = "관심사 이름")
    String interestName,

    @Schema(description = "관련 키워드 목록")
    List<String> interestKeywords,

    @Schema(description = "구독자 수", format = "int64")
    Long interestSubscriberCount,

    @Schema(description = "구독한 날짜", format = "date-time")
    Instant createdAt

) {

}
