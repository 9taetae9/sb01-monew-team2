package com.codeit.team2.monew.module.domain.article.repository;

import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleInterestRepository extends JpaRepository<ArticleInterest, UUID> {

}
