package com.codeit.team2.monew.module.domain.comment.entity;

import com.codeit.team2.monew.module.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment extends BaseEntity {

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "article_id", nullable = false)
//    private Article article;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    @Column(nullable = false)
    private String content;

    private long likeCount;

    private boolean deleted;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentLike> likes = new HashSet<>();

//    public Comment(Article article, User user, String content) {
//        this.article = article;
//        this.user = user;
//        this.content = content;
//        this.likeCount = 0;
//        this.deleted = false;
//    }

//    public static Comment create(Article article, User user, String content) {
//        Comment comment = new Comment();
//        comment.article = article;
//        comment.user = user;
//        comment.content = content;
//        comment.likeCount = 0;
//        comment.deleted = false;
//        return comment;
//    }

    public void update(String content) {
        this.content = content;
    }

    public void delete() {
        this.deleted = true;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void addLike(CommentLike like) {
        this.likes.add(like);
        this.incrementLikeCount();
    }

    public void removeLike(CommentLike like) {
        this.likes.remove(like);
        this.decrementLikeCount();
    }

}
