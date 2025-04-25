package com.codeit.team2.monew.module.domain.useractivity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "사용자 활동 내역")
public record UserActivityDto(

    @Schema(description = "사용자 ID", format = "uuid")
    UUID id,

    @Schema(description = "이메일")
    String email,

    @Schema(description = "닉네임")
    String nickname,

    @Schema(description = "가입한 날짜", format = "date-time")
    Instant createdAt,

    @Schema(description = "구독 정보")
    List<SubscriptionItemDto> subscriptions,

    @Schema(description = "최근 작성한 댓글 (최대 10건)")
    List<CommentItemDto> comments,

    @Schema(description = "최근 좋아요를 누른 댓글 (최대 10건)")
    List<CommentLikeItemDto> commentLikes,

    @Schema(description = "최근 본 기사 (최대 10건)")
    List<ArticleViewItemDto> articleViews

) {

}
