package com.codeit.team2.monew.module.domain.comment.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CommentUpdateRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CursorPageRequestCommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CursorPageResponseCommentDto;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.mapper.CommentMapper;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Override
    public Comment register(CommentRegisterRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> {
                log.debug("User Not Found - userId: {}", request.userId());
                return new EntityNotFoundException("User Not Found");
            });

        Article article = articleRepository.findById(request.articleId())
            .orElseThrow(() -> {
                log.debug("Article Not Found - userId: {}", request.articleId());
                return new EntityNotFoundException("Article Not Found");
            });

        Comment comment = commentMapper.toEntity(request, article, user);

        return commentRepository.save(comment);
    }

    @Override
    public Comment edit(UUID commentId, UUID userId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> {
                log.debug("Comment Not Found - commentId: {}", commentId);
                return new EntityNotFoundException("Comment Not Found");
            });

        if (!comment.getUser().getId().equals(userId)) {
            log.debug("Edit Permission Denied - Attempted UserId: {}, Author UserId: {}",
                userId, comment.getUser().getId());
            throw new SecurityException("Edit Permission Denied");
        }

        comment.update(request.content());
        return comment;
    }

    @Override
    public void delete(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> {
                log.debug("Comment Not Found - commentId: {}", commentId);
                return new EntityNotFoundException("Comment Not Found");
            });

        if (!comment.getUser().getId().equals(userId)) {
            log.debug("Delete Permission Denied - Attempted UserId: {}, Author UserId: {}",
                userId, comment.getUser().getId());
            throw new SecurityException("Delete Permission Denied");
        }

        comment.delete();
    }

    @Override
    public void hardDelete(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> {
                log.debug("Comment Not Found - commentId: {}", commentId);
                return new EntityNotFoundException("Comment Not Found");
            });

        commentRepository.delete(comment);
    }

    @Override
    public CursorPageResponseCommentDto findAll(UUID userId,
        CursorPageRequestCommentDto cursorPageRequestCommentDto) {
        return null;
    }
}
