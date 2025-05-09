package com.codeit.team2.monew.module.domain.interest.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.config.QuerydslConfig;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleInterestRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test-temp")
@Import({JpaConfig.class, QuerydslConfig.class})
public class InterestRepositoryTest {

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleInterestRepository articleInterestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private InterestKeywordRepository interestKeywordRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    EntityManager em;

    @DisplayName("관심사 삭제 시, 키워드와 구독 테이블 확인")
    @Test
    void deleteInterest_check_success() {
        // given
        User user = userRepository.save(new User("test@test.com", "test", "pw", false));
        Keyword keyword = keywordRepository.saveAndFlush(new Keyword("당근"));

        Interest interest = Interest.create("채소");
        interest.addKeyword(keyword);
        interest.addSubscriber(user);
        Interest savedInterest = interestRepository.saveAndFlush(interest);

        Article article = new Article("a", "a", "a", "a", Set.of(), 0L, Instant.now(), false, null);
        articleRepository.saveAndFlush(article);

        ArticleInterest articleInterest = new ArticleInterest(article, savedInterest);
        articleInterestRepository.saveAndFlush(articleInterest);
        savedInterest.getArticleInterests().add(articleInterest);
        Interest finalInterest = interestRepository.saveAndFlush(savedInterest);

        // when
        interestRepository.delete(finalInterest);

        // then
        assertThat(interestRepository.findById(savedInterest.getId())).isNotPresent();
        assertThat(interestKeywordRepository.findAll()).isEmpty();
        assertThat(articleInterestRepository.findAll()).isEmpty();
        assertThat(subscriptionRepository.findAll()).isEmpty();
        assertThat(keywordRepository.findByName(keyword.getName())).isPresent();
        assertThat(userRepository.findById(user.getId())).isPresent();

    }

    @DisplayName("관심사를 키워드까지 fetch join으로 조회한다")
    @Test
    void findByIdWithKeywords_success() {
        // given
        User user = userRepository.save(new User("test@test.com", "test", "test", false));
        Keyword keyword1 = keywordRepository.saveAndFlush(new Keyword("감자"));
        Keyword keyword2 = keywordRepository.saveAndFlush(new Keyword("고구마"));

        Interest interest = Interest.create("채소");
        interest.addKeyword(keyword1);
        interest.addKeyword(keyword2);
        interest.addSubscriber(user);
        Interest savedInterest = interestRepository.saveAndFlush(interest);

        em.flush();
        em.clear();

        // when
        var found = interestRepository.findByIdWithKeywords(savedInterest.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getKeywords()).hasSize(2);
        assertThat(found.get().getKeywords())
            .extracting(k -> k.getKeyword().getName())
            .containsExactlyInAnyOrder("감자", "고구마");
    }

}
