package com.codeit.team2.monew.module.domain.article.repository;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID> {

    Optional<ArticleView> findByUserAndArticle(User user, Article article);
}
