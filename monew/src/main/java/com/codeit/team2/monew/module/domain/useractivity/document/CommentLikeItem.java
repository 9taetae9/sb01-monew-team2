package com.codeit.team2.monew.module.domain.useractivity.document;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comment_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CommentLikeItem {

    private UUID id;
    private Instant createdAt;
    private UUID commentId;
    private UUID articleId;
    private String articleTitle;
    private UUID commentUserId;
    private String commentUserNickname;
    private String commentContent;
    private Long commentLikeCount;
    private Instant commentCreatedAt;
}
