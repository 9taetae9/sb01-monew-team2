package com.codeit.team2.monew.module.domain.article.repository;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, UUID> {

}
