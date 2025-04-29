package com.codeit.team2.monew.module.domain.user;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleViewRepository;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CommentUpdateRequest;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.comment.service.CommentLikeService;
import com.codeit.team2.monew.module.domain.comment.service.CommentService;
import com.codeit.team2.monew.module.domain.interest.dto.request.InterestRegisterRequest;
import com.codeit.team2.monew.module.domain.interest.dto.response.InterestDto;
import com.codeit.team2.monew.module.domain.interest.service.InterestService;
import com.codeit.team2.monew.module.domain.subscription.service.SubscriptionService;
import com.codeit.team2.monew.module.domain.user.dto.request.UserRegisterRequest;
import com.codeit.team2.monew.module.domain.user.dto.request.UserUpdateRequest;
import com.codeit.team2.monew.module.domain.user.dto.response.UserDto;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import com.codeit.team2.monew.module.domain.user.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ArticleRepository articleRepository;
    private final ArticleViewRepository articleViewRepository;
    private final UserService userService;
    private final InterestService interestService;
    private final SubscriptionService subscriptionService;
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

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
                0L,
                Instant.now(),
                false
            );
            articleRepository.save(article);

            Comment comment = Comment.create(
                article,
                user,
                "content" + i
            );
            comment.incrementLikeCount();
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

    void initCommentRegisterEventData() {
        userService.registerUser(
            new UserRegisterRequest(
                "email@a.com",
                "nickname",
                "passowrd"
            )
        );
        Article article = new Article(
            "title",
            "NAVER",
            "sourceUrl",
            "summary",
            Set.of(),
            0L,
            Instant.now(),
            false
        );
        articleRepository.save(article);
    }

    void initCommentLikeRegisterEventData() {
        UserDto userDto = userService.registerUser(
            new UserRegisterRequest(
                "email@a.com",
                "nickname",
                "passowrd"
            )
        );
        Article article = articleRepository.save(
            new Article(
                "title",
                "NAVER",
                "sourceUrl",
                "summary",
                Set.of(),
                0L,
                Instant.now(),
                false
            ));

        CommentRegisterRequest commentRegisterRequest = new CommentRegisterRequest(
            article.getId(),
            userDto.id(),
            "comment1"
        );

        Comment comment = commentService.register(commentRegisterRequest);
        commentLikeService.like(comment.getId(), userDto.id());
    }

    void intiSubscriptionRegisterEventData() {
        UserDto userDto = userService.registerUser(new UserRegisterRequest(
                "email@a.com",
                "nickname1",
                "password"
            )
        );

        InterestRegisterRequest request = new InterestRegisterRequest(
            "IT",
            List.of("java", "python")
        );

        InterestDto interestDto = interestService.create(request, userDto.id());

        subscriptionService.subscription(interestDto.id(), userDto.id());
    }

    void initUserRegisterEventData() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
            "email",
            "nickname",
            "password"
        );
        userService.registerUser(userRegisterRequest);
    }

    void initUserNicknameUpdateEventData() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
            "email",
            "nickname",
            "password"
        );
        UserDto userDto = userService.registerUser(userRegisterRequest);

        Article article = articleRepository.save(
            new Article(
                "title",
                "NAVER",
                "sourceUrl",
                "summary",
                Set.of(),
                0L,
                Instant.now(),
                false
            ));

        CommentRegisterRequest commentRegisterRequest = new CommentRegisterRequest(
            article.getId(),
            userDto.id(),
            "comment1"
        );

        Comment comment = commentService.register(commentRegisterRequest);
        CommentLike commentLike = commentLikeService.like(comment.getId(), userDto.id());

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newNickname1");
        userService.updateUser(
            userDto.id(),
            userDto.id(),
            userUpdateRequest
        );
    }

    void initCommentContentUpdateEventData() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
            "email",
            "nickname",
            "password"
        );
        UserDto userDto = userService.registerUser(userRegisterRequest);

        Article article = articleRepository.save(
            new Article(
                "title",
                "NAVER",
                "sourceUrl",
                "summary",
                Set.of(),
                0L,
                Instant.now(),
                false
            ));

        CommentRegisterRequest commentRegisterRequest = new CommentRegisterRequest(
            article.getId(),
            userDto.id(),
            "comment1"
        );

        Comment comment = commentService.register(commentRegisterRequest);
        CommentLike commentLike = commentLikeService.like(comment.getId(), userDto.id());

        CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest("newContent");
        commentService.edit(comment.getId(), userDto.id(), commentUpdateRequest);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
    }
}
