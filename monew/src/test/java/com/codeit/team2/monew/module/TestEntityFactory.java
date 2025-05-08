package com.codeit.team2.monew.module;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class TestEntityFactory {

    public static DummyArticle dummyArticle(String title) {
        return new DummyArticle(
            UUID.randomUUID(),
            title,
            "TEST",
            "http://test.com",
            "Test Summary",
            0L,
            Instant.now(),
            false, null
        );
    }

    public static DummyArticle dummyArticleNoId(String title) {
        return new DummyArticle(
            null,
            title,
            "TEST",
            "http://test.com",
            "Test Summary",
            0L,
            Instant.now(),
            false, null
        );
    }

    public static Article createArticle(String title) {
        Article article = new Article(
            title,
            "TEST",
            "http://test.com",
            "Test Summary",
            Set.of(),
            0L,
            Instant.now(),
            false,
            null
        );

        ReflectionTestUtils.setField(article, "id", UUID.randomUUID());
        return article;
    }

    public static Comment createComment(Article article, User user, String content) {
        Comment comment = Comment.create(
            article,
            user,
            content
        );

        ReflectionTestUtils.setField(comment, "id", UUID.randomUUID());
        return comment;
    }

    public static CommentLike createCommentLike(Comment comment, User user) {
        CommentLike commentLike = CommentLike.create(comment, user);

        ReflectionTestUtils.setField(commentLike, "id", UUID.randomUUID());
        return commentLike;
    }

    public static Subscription createSubscription(User user, Interest interest) {
        Subscription subscription = new Subscription(user, interest);

        ReflectionTestUtils.setField(subscription, "id", UUID.randomUUID());
        return subscription;
    }

    public static Interest createInterest(String name) {
        Interest interest = Interest.create(name);

        ReflectionTestUtils.setField(interest, "id", UUID.randomUUID());
        return interest;
    }

    public static Interest createInterestNoId(String name) {
        Interest interest = Interest.create(name);

        // ReflectionTestUtils.setField(interest, "id", UUID.randomUUID());
        return interest;
    }

    public static Keyword createKeyword(String keyword) {
        Keyword k = new Keyword("AI");
        //ReflectionTestUtils.setField(k, "id", UUID.randomUUID());
        return k;
    }

    public static InterestKeyword createInterestKeyword(Interest interest, Keyword keyword) {
        InterestKeyword ik = new InterestKeyword(interest, keyword);
        // ReflectionTestUtils.setField(ik, "id", UUID.randomUUID());
        return ik;
    }
}
