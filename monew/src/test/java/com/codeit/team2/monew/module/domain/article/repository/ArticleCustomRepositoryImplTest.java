package com.codeit.team2.monew.module.domain.article.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.config.QuerydslConfig;
import com.codeit.team2.monew.module.domain.article.dto.request.ArticleOrderBy;
import com.codeit.team2.monew.module.domain.article.dto.request.CursorPageRequestArticleDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.repository.InterestRepository;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test-temp")
@Import({JpaConfig.class, QuerydslConfig.class})
@Transactional
class ArticleCustomRepositoryImplTest {

    @Autowired
    @Qualifier("articleCustomRepositoryImpl")
    private ArticleCustomRepository articleCustomRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private ArticleInterestRepository articleInterestRepository;

    @Autowired
    private TestEntityManager em;

    private UUID interestId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(new User("email", "name", "password", false));

        Interest interest = interestRepository.save(Interest.create("interest"));
        this.interestId = interest.getId();

        for (int i = 0; i < 10; i++) {
            Article article = new Article("title " + i, "source", "http://test" + i + ".com",
                "summary",
                Set.of(), (long) i, Instant.now(), false);
            em.persist(article);

            ArticleInterest ai = new ArticleInterest(article, interest);
            em.persist(ai);
        }
    }

    @Test
    @DisplayName("기사 목록 조회 - 출판일 ASC 정렬")
    void testFindWithCursor_orderByPublishDateAsc() {
        CursorPageRequestArticleDto request = new CursorPageRequestArticleDto(null, interestId,
            null, null, null, ArticleOrderBy.publishDate, Direction.ASC, null, null, 3);

        Slice<Article> slice = articleCustomRepository.findWithCursor(request);

        assertThat(slice.getContent()).hasSize(3);
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("기사 목록 조회 - 조회수 DESC 정렬")
    void testFindWithCursor_orderByViewCountDesc() {
        CursorPageRequestArticleDto request = new CursorPageRequestArticleDto(
            null,
            null,
            null,
            null,
            null,
            ArticleOrderBy.viewCount,
            Direction.DESC,
            null,
            null,
            5
        );

        Slice<Article> slice = articleCustomRepository.findWithCursor(request);

        assertThat(slice.getContent()).hasSize(5);
        assertThat(slice.hasNext()).isTrue();

        // 조회수가 많은 순으로 정렬되었는지 검증
        List<Article> articles = slice.getContent();
        assertThat(articles).isNotEmpty();
        long previousCount = articles.get(0).getViewCount();
        for (Article a : articles) {
            long currentCount = a.getViewCount();
            assertThat(currentCount).isLessThanOrEqualTo(previousCount);
            previousCount = currentCount;
        }
    }

    @Test
    @DisplayName("기사 목록 조회 - 댓글수 DESC 정렬")
    void testFindWithCursor_orderByCommentCountDesc() {
        CursorPageRequestArticleDto request = new CursorPageRequestArticleDto(
            null,
            null,
            null,
            null,
            null,
            ArticleOrderBy.commentCount,
            Direction.DESC,
            null,
            null,
            5
        );

        Slice<Article> slice = articleCustomRepository.findWithCursor(request);

        // 댓글 수가 많은 순으로 정렬되었는지 검증
        List<Article> articles = slice.getContent();
        assertThat(articles).isNotEmpty();
        long previousCommentCount = Long.MAX_VALUE;
        for (Article article : articles) {
            long currentCount = commentRepository.countByArticle(article);
            assertThat(currentCount).isLessThanOrEqualTo(previousCommentCount);
            previousCommentCount = currentCount;
        }
    }

    @Test
    @DisplayName("기사 목록 조회 - 키워드 필터링")
    void testFindWithCursor_filterByKeyword() {
        CursorPageRequestArticleDto request = new CursorPageRequestArticleDto(
            "1",  // keyword
            null,
            null,
            null,
            null,
            ArticleOrderBy.publishDate,
            Direction.DESC,
            null,
            null,
            10
        );

        Slice<Article> slice = articleCustomRepository.findWithCursor(request);

        assertThat(slice.getContent())
            .extracting(Article::getTitle)
            .allMatch(title -> title.contains("1"));
    }

    @Test
    @DisplayName("기사 목록 조회 totalElements 계산 - 키워드 필터링 o")
    void testCountFilteredTotalElements_withKeywordFilter() {
        String keyword = "title";

        long count = articleCustomRepository.countFilteredTotalElements(
            keyword,
            null,
            null,
            null,
            null
        );

        assertThat(count).isEqualTo(10); // setUp에선 1개의 Article만 있으므로
    }

    @Test
    @DisplayName("기사 목록 조회 totalElements 계산 - 키워드 필터링 x")
    void testCountFilteredTotalElements_noMatch() {
        // keyword가 전혀 매칭되지 않는 경우
        String keyword = "nonexistent";

        long count = articleCustomRepository.countFilteredTotalElements(
            keyword,
            null,
            null,
            null,
            null
        );

        assertThat(count).isEqualTo(0);
    }
}
