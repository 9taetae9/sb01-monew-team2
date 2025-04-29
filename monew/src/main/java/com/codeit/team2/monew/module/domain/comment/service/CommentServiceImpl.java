package com.codeit.team2.monew.module.domain.comment.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.comment.dto.CommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CommentOrderBy;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CommentUpdateRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CursorPageRequestCommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CursorPageResponseCommentDto;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.event.CommentRegisterEvent;
import com.codeit.team2.monew.module.domain.comment.event.CommentUpdateEvent;
import com.codeit.team2.monew.module.domain.comment.mapper.CommentMapper;
import com.codeit.team2.monew.module.domain.comment.repository.CommentCustomRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
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
    private final CommentCustomRepository commentCustomRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ApplicationEventPublisher publisher;

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

        // comment 생성 이벤트 발생
        publisher.publishEvent(new CommentRegisterEvent(comment, article, user));

        return commentRepository.save(comment);
    }

    @Override
    public CommentDto edit(UUID commentId, UUID userId, CommentUpdateRequest request) {
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

        boolean likedByMe = commentLikeRepository.existsByCommentIdAndUserId(comment.getId(),
            userId);

        // comment 수정 이벤트 발생
        publisher.publishEvent(new CommentUpdateEvent(
            comment,
            userId
        ));

        return commentMapper.toDto(comment, likedByMe);
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

        Slice<Comment> slices = commentCustomRepository.findAll(
            cursorPageRequestCommentDto.articleId(),
            cursorPageRequestCommentDto.orderBy(),
            cursorPageRequestCommentDto.direction(),
            cursorPageRequestCommentDto.cursor(),
            cursorPageRequestCommentDto.after(),
            cursorPageRequestCommentDto.limit());

        List<CommentDto> commentDtos = new ArrayList<>();
        slices.getContent().forEach(comment -> {
            boolean likedByMe = commentLikeRepository.existsByCommentIdAndUserId(comment.getId(),
                userId);
            commentDtos.add(commentMapper.toDto(comment, likedByMe));
        });

        Long totalElements = commentRepository.countByArticleId(
            cursorPageRequestCommentDto.articleId());

        boolean hasNext = slices.hasNext();

        Object nextCursor = null;
        Instant nextAfter = null;

        if (hasNext) {
            Comment lastComment = slices.getContent().get(slices.getContent().size() - 1);
            if (cursorPageRequestCommentDto.orderBy().equals(CommentOrderBy.createdAt)) {
                nextAfter = lastComment.getCreatedAt();
                nextCursor = nextAfter;
            } else if (cursorPageRequestCommentDto.orderBy().equals(CommentOrderBy.likeCount)) {
                nextCursor = lastComment.getLikeCount();
                nextAfter = lastComment.getCreatedAt();
            }
        }

        return new CursorPageResponseCommentDto(commentDtos, nextCursor, nextAfter,
            slices.getSize(), totalElements,
            hasNext);
    }
}
