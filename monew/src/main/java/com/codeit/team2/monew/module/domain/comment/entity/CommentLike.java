package com.codeit.team2.monew.module.domain.comment.entity;

import com.codeit.team2.monew.module.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_likes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"comment_id", "user_id"}))
public class CommentLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    private Instant likedAt;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

//    public static CommentLike create(Comment comment, User user) {
//        CommentLike commentLike = new CommentLike();
//        commentLike.comment = comment;
//        commentLike.user = user;
//        commentLike.likedAt = Instant.now();
//
//        if (comment != null) {
//            comment.addLike(commentLike);
//        }
//
//        return commentLike;
//    }
}
