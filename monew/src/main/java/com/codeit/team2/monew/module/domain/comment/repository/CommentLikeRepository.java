package com.codeit.team2.monew.module.domain.comment.repository;

import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

}
