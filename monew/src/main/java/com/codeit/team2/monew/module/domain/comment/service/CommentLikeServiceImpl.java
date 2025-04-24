package com.codeit.team2.monew.module.domain.comment.service;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.notification.service.NotificationService;
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
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public CommentLike like(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> {
                log.debug("Comment Not Found: commentId={}", commentId);
                return new EntityNotFoundException("Comment Not Found");
            });

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.debug("User Not Found: userId={}", userId);
                return new EntityNotFoundException("User Not Found");
            });

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            log.debug("Already liked this comment: commentId = {}, userId={}",
                commentId, userId);
            throw new IllegalStateException("Already liked this comment");
        }

        CommentLike commentLike = CommentLike.create(comment, user);
        comment.incrementLikeCount();

        // 알림 생성
        notificationService.createCommentNotification(comment, comment.getUser(), user);

        return commentLikeRepository.save(commentLike);
    }

    @Override
    public void unlike(UUID commentId, UUID userId) {

        CommentLike commentLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
            .orElseThrow(() -> {
                log.debug("CommentLike Not Found: commentId={}, userId={}", commentId, userId);
                return new EntityNotFoundException("CommentLike Not Found");
            });

        Comment comment = commentLike.getComment();
        comment.decrementLikeCount();

        commentLikeRepository.delete(commentLike);
    }
}
