package com.codeit.team2.monew.module.domain.comment.repository;

import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);

    Optional<CommentLike> findByCommentIdAndUserId(UUID commentId, UUID userId);
    List<CommentLike> findTop10ByUserOrderByLikedAtDesc(User user);

}
