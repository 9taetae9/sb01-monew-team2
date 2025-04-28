package com.codeit.team2.monew.module.domain.useractivity.document;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CommentItem {

    private UUID id;
    private UUID articleId;
    private String articleTitle;
    private UUID userId;
    private String userNickname;
    private String content;
    private Long likeCount;
    private Instant createdAt;

    public void updateLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void updateUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}

