package com.codeit.team2.monew.module.domain.comment.controller;

import com.codeit.team2.monew.module.domain.comment.controller.docs.CommentLikeControllerDocs;
import com.codeit.team2.monew.module.domain.comment.dto.CommentLikeDto;
import com.codeit.team2.monew.module.domain.comment.entity.CommentLike;
import com.codeit.team2.monew.module.domain.comment.mapper.CommentMapper;
import com.codeit.team2.monew.module.domain.comment.service.CommentLikeService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments/{commentId}/comment-likes")
public class CommentLikeController implements CommentLikeControllerDocs {

    private final CommentLikeService commentLikeService;
    private final CommentMapper commentMapper;

    @PostMapping
    public ResponseEntity<CommentLikeDto> like(
        @PathVariable UUID commentId,
        @RequestHeader("Monew-Request-User-Id") UUID userId
    ) {
        log.info("Start - CommentLikeController/like: commentId={}, userId={}",
            commentId, userId);

        CommentLike commentLike = commentLikeService.like(commentId, userId);
        CommentLikeDto result = commentMapper.toDto(commentLike);

        log.info("Complete - CommentLikeController/like: commentLikeId={}", result.id());

        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<Void> unlike(
        @PathVariable UUID commentId,
        @RequestHeader("Monew-Request-User-Id") UUID userId
    ) {
        log.info("Start - CommentLikeController/unlike: commentId={}, userId={}",
            commentId, userId);

        commentLikeService.unlike(commentId, userId);

        log.info("Complete - CommentLikeController/unlike: commentId={}, userId={}", commentId,
            userId);
        return ResponseEntity.ok().build();
    }
}
