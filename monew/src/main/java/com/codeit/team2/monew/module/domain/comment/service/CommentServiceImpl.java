package com.codeit.team2.monew.module.domain.comment.service;

import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CommentUpdateRequest;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import java.util.UUID;

public class CommentServiceImpl implements CommentService {

    @Override
    public Comment register(CommentRegisterRequest request) {
        return null;
    }

    @Override
    public Comment edit(UUID commentId, UUID userId, CommentUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(UUID commentId, UUID userId) {

    }
}
