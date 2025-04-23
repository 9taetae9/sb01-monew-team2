package com.codeit.team2.monew.module.domain.user;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleViewRepository;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInitializer {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ArticleRepository articleRepository;
    private final ArticleViewRepository articleViewRepository;


    @PostConstruct
    void init() {
        User user = userRepository.save(
            new User("email@a.com", "nickname", "passowrd", false)
        );

        // 3. 기사 & 댓글 & 좋아요 & 조회 기록 생성
        for (int i = 0; i < 20; i++) {
            Article article = new Article(
                "title" + i,
                "NAVER",
                "sourceUrl" + i,
                "summary" + i,
                Set.of(),
                0,
                Instant.now(),
                false
            );
            articleRepository.save(article);

            Comment comment = Comment.create(
                article,
                user,
                "content" + i
            );
            commentRepository.save(comment);

            CommentLike commentLike = CommentLike.create(
                comment,
                user
            );
            commentLikeRepository.save(commentLike);

            ArticleView articleView = new ArticleView(
                user,
                article,
                Instant.now()
            );
            articleViewRepository.save(articleView);
        }
    }

}
