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

    public void addCommentItem(CommentItem commentItem) {
        if (this.comments.size() >= 10) {
            this.comments.remove(this.comments.size() - 1);
        }
        this.comments.add(0, commentItem);
    }

    public void addSubscriptionItem(SubscriptionItem subscriptionItem) {
        if (this.subscriptions.size() >= 10) {
            this.subscriptions.remove(this.subscriptions.size() - 1);
        }
        this.subscriptions.add(0, subscriptionItem);
    }

    public void addArticleViewItem(ArticleViewItem articleViewItem) {
        if (this.articleViews.size() >= 10) {
            this.articleViews.remove(this.articleViews.size() - 1);
        }
        this.articleViews.add(0, articleViewItem);
    }
}

