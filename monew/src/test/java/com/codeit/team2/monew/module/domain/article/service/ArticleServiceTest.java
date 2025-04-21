package com.codeit.team2.monew.module.domain.article.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ArticleService articleService;

    @Test
    void test_articleViewShouldIncrease_whenNewUserViews() {
        // given
        ArticleInterest interest = mock(ArticleInterest.class);
        Article article = new Article(
            "test", "NAVER", "https://test.com", "this is test summary", Set.of(interest), 0,
            Instant.now(), false
        );
        ReflectionTestUtils.setField(article, "id", UUID.randomUUID());
        UUID randomUserId = UUID.randomUUID();
        User user = new User("test@gmail.com", "testUser", "test", false);
        ReflectionTestUtils.setField(user, "id", randomUserId);

        ArticleViewDto returnDto = new ArticleViewDto(
            UUID.randomUUID(),
            randomUserId,
            Instant.now(),
            article.getId(),
            article.getSource(),
            article.getSourceUrl(),
            article.getTitle(),
            article.getPublishedDate(),
            article.getSummary(),
            0,
            1
        );

        BDDMockito.given(articleRepository.findById(article.getId()))
            .willReturn(Optional.of(article));
        BDDMockito.given(userRepository.findById(randomUserId))
            .willReturn(Optional.of(user));

        // when
        ArticleViewDto response = articleService.createUserArticleView(randomUserId,
            article.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.articleId()).isEqualTo(article.getId());
        assertThat(response.articleViewCount()).isGreaterThan(0);
    }

}
