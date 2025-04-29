package com.codeit.team2.monew.module.domain.article.repository;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleInterestRepository extends JpaRepository<ArticleInterest, UUID> {

    boolean existsByArticleAndInterest(Article article, Interest interest);

    @Query("""
            SELECT MAX(ai.createdAt)
            FROM ArticleInterest ai
            WHERE ai.interest IN :interests
            GROUP BY ai.interest
        """)
    List<Instant> fetchLeastRecentCreatedAtInInterests(
        @Param("interests") List<Interest> interests);
}
