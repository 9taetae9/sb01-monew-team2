package com.codeit.team2.monew.module.domain.useractivity.document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_activities")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class UserActivity {

    private UUID id; // 사용자 아이디
    private String email;
    private String nickname;
    private Instant createdAt;
    private List<SubscriptionItem> subscriptions;
    private List<CommentItem> comments;
    private List<CommentLikeItem> commentLikes;
    private List<ArticleViewItem> articleViews;
}

