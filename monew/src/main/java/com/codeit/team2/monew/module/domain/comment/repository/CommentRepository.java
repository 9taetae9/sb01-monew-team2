package com.codeit.team2.monew.module.domain.comment.repository;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentCustomRepository {

    @EntityGraph(attributePaths = {"article"})
    List<Comment> findTop10ByUserOrderByCreatedAtDesc(User user);

    Long countByArticle(Article article);

    Long countByArticleId(UUID articleId);

    @Query("SELECT c.article.id, COUNT(c) FROM Comment c WHERE c.article.id IN :articleIds GROUP BY c.article.id")
    List<Object[]> countByArticleIds(@Param("articleIds") List<UUID> articleIds);


}
