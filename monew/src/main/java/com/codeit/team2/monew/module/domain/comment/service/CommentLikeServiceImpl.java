package com.codeit.team2.monew.module.domain.comment.service;

import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.event.CommentLikeDeleteEvent;
import com.codeit.team2.monew.module.domain.comment.event.CommentLikeRegisterEvent;
import com.codeit.team2.monew.module.domain.comment.exception.CommentLikeAlreadyExistsException;
import com.codeit.team2.monew.module.domain.comment.exception.CommentLikeNotFoundException;
import com.codeit.team2.monew.module.domain.comment.exception.CommentNotFoundException;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.notification.service.NotificationService;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.exception.UserNotFoundException;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher publisher;

    @Override
    public CommentLike like(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findByIdAndDeletedFalse(commentId)
            .orElseThrow(() -> {
                log.debug("Comment Not Found or Deleted: commentId={}", commentId);
                return new CommentNotFoundException(commentId);
            });

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.debug("User Not Found: userId={}", userId);
                return new UserNotFoundException(userId);
            });

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            log.debug("Already liked this comment: commentId = {}, userId={}",
                commentId, userId);
            throw new CommentLikeAlreadyExistsException(commentId, userId);
        }

        CommentLike commentLike = CommentLike.create(comment, user);
        comment.incrementLikeCount();

        // 알림 생성
        notificationService.createCommentNotification(comment, comment.getUser(), user);

        // 댓글 좋아요 이벤트 발행
        publisher.publishEvent(new CommentLikeRegisterEvent(commentLike));

        return commentLikeRepository.save(commentLike);
    }

    @Override
    public void unlike(UUID commentId, UUID userId) {

        CommentLike commentLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
            .orElseThrow(() -> {
                log.debug("CommentLike Not Found: commentId={}, userId={}", commentId, userId);
                return new CommentLikeNotFoundException(commentId, userId);
            });

        Comment comment = commentLike.getComment();

        if (comment.isDeleted()) {
            log.debug("Comment is deleted - cannot unlike: commentId={}", commentId);
            throw new CommentNotFoundException(commentId);
        }

        comment.decrementLikeCount();

        // 댓글 좋아요 취소 이벤트 발생
        publisher.publishEvent(new CommentLikeDeleteEvent(commentLike));

        commentLikeRepository.delete(commentLike);
    }
}
