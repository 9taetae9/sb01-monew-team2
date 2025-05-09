package com.codeit.team2.monew.module.domain.comment.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.config.QuerydslConfig;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.comment.dto.CommentOrderBy;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
class CommentCustomRepositoryImplTest {

    @Qualifier("commentCustomRepositoryImpl")
    @Autowired
    private CommentCustomRepository commentCustomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    private UUID articleId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(new User("email", "nickname", "password", false));
        Article article = articleRepository.save(
            new Article("title", "source", "url", "summary", Set.of(), 0L, Instant.now(), false,
                ""));
        articleId = article.getId();

        for (int i = 0; i < 5; i++) {
            Comment comment = Comment.create(article, user, "content");
            em.persist(comment);
        }

        em.flush();
        em.clear();
    }

    @Test
    void testFindAll_orderByCreatedAtAsc() {
        Slice<Comment> slice = commentCustomRepository.findAll(
            articleId,
            CommentOrderBy.createdAt,
            Direction.ASC,
            null,
            null,
            3
        );

        assertThat(slice.getContent()).hasSize(3);
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    void testFindAll_orderByLikeCountDesc_withCursor() {
        // 첫 페이지 가져오기
        Slice<Comment> slice = commentCustomRepository.findAll(
            articleId,
            CommentOrderBy.likeCount,
            Direction.DESC,
            null,
            null,
            2
        );

        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.hasNext()).isTrue();

        // 다음 페이지 가져오기 (커서 기반)
        Comment last = slice.getContent().get(slice.getContent().size() - 1);
        Long nextCursor = last.getLikeCount();
        Instant nextAfter = last.getCreatedAt();

        Slice<Comment> nextSlice = commentCustomRepository.findAll(
            articleId,
            CommentOrderBy.likeCount,
            Direction.DESC,
            nextCursor.toString(),
            nextAfter,
            2
        );

        assertThat(nextSlice.getContent()).isNotEmpty();
    }

    @Test
    void testFindAll_lastPage_hasNextFalse() {
        // 전체 5개라서 limit=5로 요청하면 마지막 페이지임
        Slice<Comment> slice = commentCustomRepository.findAll(
            articleId,
            CommentOrderBy.createdAt,
            Direction.ASC,
            null,
            null,
            5
        );

        assertThat(slice.getContent()).hasSize(5);
        assertThat(slice.hasNext()).isFalse();
    }

    @Test
    void testFindAll_orderByLikeCountAsc_withDifferentLikeCounts() {
        // 댓글 중 일부의 likeCount를 조작
        List<Comment> comments = commentRepository.findAll();
        comments.get(0).incrementLikeCount(); // 1
        comments.get(1).incrementLikeCount(); // 1
        comments.get(1).incrementLikeCount(); // 2

        em.flush();
        em.clear();

        Slice<Comment> slice = commentCustomRepository.findAll(
            articleId,
            CommentOrderBy.likeCount,
            Direction.ASC,
            null,
            null,
            5
        );

        List<Comment> content = slice.getContent();
        assertThat(content).isSortedAccordingTo(Comparator.comparing(Comment::getLikeCount));
    }

    @Test
    void testFindAll_orderByCreatedAtDesc() {
        Slice<Comment> slice = commentCustomRepository.findAll(
            articleId,
            CommentOrderBy.createdAt,
            Direction.DESC,
            null,
            null,
            5
        );

        List<Comment> content = slice.getContent();
        assertThat(content).isSortedAccordingTo(
            Comparator.comparing(Comment::getCreatedAt).reversed());
    }
}
