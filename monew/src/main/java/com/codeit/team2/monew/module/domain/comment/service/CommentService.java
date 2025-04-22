package com.codeit.team2.monew.module.domain.comment.service;

import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CommentUpdateRequest;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {

    Comment register(CommentRegisterRequest request);

    Comment edit(UUID commentId, UUID userId, CommentUpdateRequest request);

    void delete(UUID commentId, UUID userId);

    void hardDelete(UUID commentId);

//    public List<CommentDto> getCommentsByArticleId(UUID articleId, UUID userId, UUID cursor,
//        Instant cursorTime, int limit);
//
//    public List<CommentDto> getCommentsByArticleIdOrderByLikes(UUID articleId, UUID userId,
//        int limit);


}
