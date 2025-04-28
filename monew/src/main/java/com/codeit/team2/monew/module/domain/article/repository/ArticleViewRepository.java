package com.codeit.team2.monew.module.domain.article.repository;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID> {

    Optional<ArticleView> findByUserAndArticle(User user, Article article);

    @EntityGraph(attributePaths = {"article", "user"})
    List<ArticleView> findTop10ByUserOrderByViewedAtDesc(User user);

    boolean existsByUserIdAndArticleId(UUID userId, UUID articleId);

    @Query("SELECT av.article.id FROM ArticleView av WHERE av.user.id = :userId AND av.article.id IN :articleIds")
    List<UUID> findViewedArticleIds(@Param("userId") UUID userId,
        @Param("articleIds") List<UUID> articleIds);

}
