package com.codeit.team2.monew.module.domain.comment.repository;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

}
