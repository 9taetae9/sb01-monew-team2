package com.codeit.team2.monew.module.domain.comment.service;

import com.codeit.team2.monew.module.domain.comment.dto.CommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CommentUpdateRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CursorPageRequestCommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CursorPageResponseCommentDto;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {

    Comment register(CommentRegisterRequest request);

    CommentDto edit(UUID commentId, UUID userId, CommentUpdateRequest request);

    void delete(UUID commentId, UUID userId);

    void hardDelete(UUID commentId);

    CursorPageResponseCommentDto findAll(UUID userId,
        CursorPageRequestCommentDto cursorPageRequestCommentDto);
}
