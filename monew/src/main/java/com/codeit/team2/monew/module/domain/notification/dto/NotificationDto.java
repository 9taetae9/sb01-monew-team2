package com.codeit.team2.monew.module.domain.notification.dto;

import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "알림 정보")
public record NotificationDto(

    @Schema(description = "알림 ID", format = "uuid")
    UUID id,

    @Schema(description = "생성된 날짜", format = "date-time")
    Instant createdAt,

    @Schema(description = "확인한 날짜", format = "date-time")
    Instant updatedAt,

    @Schema(description = "확인 여부")
    boolean confirmed,

    @Schema(description = "알림 대상 사용자 ID", format = "uuid")
    UUID userId,

    @Schema(description = "내용")
    String content,

    @Schema(description = "관련된 리소스 유형", allowableValues = {"interest", "comment"})
    ResourceType resourceType,

    @Schema(description = "관련된 리소스 ID", format = "uuid")
    UUID resourceId

) {

}
