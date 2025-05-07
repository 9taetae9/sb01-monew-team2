package com.codeit.team2.monew.module.domain.comment.repository;

import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);

    Optional<CommentLike> findByCommentIdAndUserId(UUID commentId, UUID userId);

    @EntityGraph(attributePaths = {
        "comment",
        "comment.article",
        "comment.user"
    })
    List<CommentLike> findTop10ByUserOrderByLikedAtDesc(User user);

    @Query("SELECT cl.comment.id FROM CommentLike cl WHERE cl.user.id = :userId AND cl.comment.id IN :commentIds")
    Set<UUID> findLikedCommentIdsByUserIdAndCommentIds(@Param("userId") UUID userId,
        @Param("commentIds") List<UUID> commentIds);

}
