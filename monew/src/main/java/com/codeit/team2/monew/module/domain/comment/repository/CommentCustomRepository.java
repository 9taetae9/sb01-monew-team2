package com.codeit.team2.monew.module.domain.comment.repository;

import com.codeit.team2.monew.module.domain.comment.dto.CommentOrderBy;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;

public interface CommentCustomRepository {

    Slice<Comment> findAll(UUID articleId, CommentOrderBy orderBy, Direction direction,
        Object cursor, Instant createdAt, int limit);
}
