package com.codeit.team2.monew.module.domain.article.repository;

import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DummyArticleRepository extends JpaRepository<DummyArticle, UUID> {

}
