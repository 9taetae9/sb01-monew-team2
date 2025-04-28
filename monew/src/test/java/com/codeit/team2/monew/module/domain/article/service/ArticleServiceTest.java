package com.codeit.team2.monew.module.domain.article.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapperImpl;
import com.codeit.team2.monew.module.domain.article.repository.ArticleCustomRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleViewRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleViewRepository articleViewRepository;
    private ArticleMapper articleMapper;
    private ArticleService articleService;
    private ArticleCustomRepository articleCustomRepository;
    private CommentRepository commentRepository;

    @Spy
    private ApplicationEventPublisher publisher;

    @BeforeEach
    void setup() {
        articleMapper = new ArticleMapperImpl();
        articleService = new ArticleServiceImpl(articleRepository, articleViewRepository,
            userRepository, articleMapper, articleCustomRepository, commentRepository,
            publisher);
    }

    @Test
    void test_articleViewShouldIncrease_whenNewUserViews() {
        // given
        ArticleInterest interest = mock(ArticleInterest.class);
        Article article = new Article(
            "test", "NAVER", "https://test.com", "this is test summary", Set.of(interest), 0L,
            Instant.now(), false
        );

        ReflectionTestUtils.setField(article, "id", UUID.randomUUID());
        UUID randomUserId = UUID.randomUUID();
        User user = new User("test@gmail.com", "testUser", "test", false);
        ReflectionTestUtils.setField(user, "id", randomUserId);

        ArticleView articleView = new ArticleView(user, article, Instant.now());

        BDDMockito.given(articleRepository.findById(article.getId()))
            .willReturn(Optional.of(article));
        BDDMockito.given(userRepository.findById(randomUserId))
            .willReturn(Optional.of(user));
        BDDMockito.given(articleViewRepository.findByUserAndArticle(any(), any()))
            .willReturn(Optional.empty());
        BDDMockito.given(articleViewRepository.save(any()))
            .willReturn(articleView);

        // when
        ArticleViewDto response = articleService.createUserArticleView(randomUserId,
            article.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.articleId()).isEqualTo(article.getId());
        assertThat(response.articleViewCount()).isEqualTo(1);
    }

    @Test
    void test_viewCount_shouldNotIncrease_OnDuplicateArticleView() {
        // given
        ArticleInterest interest = mock(ArticleInterest.class);
        Article article = new Article(
            "test", "NAVER", "https://test.com", "this is test summary", Set.of(interest), 1L,
            Instant.now(), false
        );

        ReflectionTestUtils.setField(article, "id", UUID.randomUUID());
        UUID randomUserId = UUID.randomUUID();
        User user = new User("test@gmail.com", "testUser", "test", false);
        ReflectionTestUtils.setField(user, "id", randomUserId);

        ArticleView articleView = new ArticleView(user, article, Instant.now());

        BDDMockito.given(articleRepository.findById(article.getId()))
            .willReturn(Optional.of(article));
        BDDMockito.given(userRepository.findById(randomUserId))
            .willReturn(Optional.of(user));
        BDDMockito.given(articleViewRepository.findByUserAndArticle(any(), any()))
            .willReturn(Optional.of(articleView));

        // when
        ArticleViewDto response = articleService.createUserArticleView(randomUserId,
            article.getId());

        // then
        assertThat(response.articleViewCount()).isEqualTo(1);
    }


    @Test
    void test_softDeleteSuccess() {

        // given
        UUID randomId = UUID.randomUUID();
        Article article = new Article(
            "test",
            "NAVER",
            "https://test.com",
            "test summary",
            Set.of(),
            0L,
            Instant.now(),
            false
        );

        BDDMockito.given(articleRepository.findById(randomId))
            .willReturn(Optional.of(article));

        // when

        articleService.softDelete(randomId);

        // then
        assertThat(article.getDeleted()).isTrue();
    }

    @Test
    void test_softDelete_Fail_ArticleNotFound() {

        // given
        UUID randomId = UUID.randomUUID();

        BDDMockito.given(articleRepository.findById(randomId))
            .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> articleService.softDelete(randomId))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
