package com.codeit.team2.monew.module.domain.comment.controller;

import com.codeit.team2.monew.module.domain.comment.controller.docs.CommentControllerDocs;
import com.codeit.team2.monew.module.domain.comment.dto.CommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CommentRegisterRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CommentUpdateRequest;
import com.codeit.team2.monew.module.domain.comment.dto.CursorPageRequestCommentDto;
import com.codeit.team2.monew.module.domain.comment.dto.CursorPageResponseCommentDto;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.mapper.CommentMapper;
import com.codeit.team2.monew.module.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController implements CommentControllerDocs {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping
    public ResponseEntity<CommentDto> register(
        @Valid @RequestBody CommentRegisterRequest request,
        @RequestHeader("Monew-Request-User-ID") UUID userId
    ) {
        log.info("Start - CommentController/register: userId={}, articleId={}",
            userId, request.articleId());

        Comment comment = commentService.register(request);
        CommentDto result = commentMapper.toDto(comment, false);

        log.info("Complete - CommentController/register: commentId={}, articleId={}",
            result.id(), result.articleId());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(result);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> update(
        @PathVariable UUID commentId,
        @RequestHeader("Monew-Request-User-ID") UUID userId,
        @Valid @RequestBody CommentUpdateRequest request
    ) {
        log.info("Start - CommentController/update: commentId={}, userId={}",
            commentId, userId);

        Comment comment = commentService.edit(commentId, userId, request);
        CommentDto result = commentMapper.toDto(comment, false);

        log.info("Complete - CommentController/update: commentId={}, content={}",
            result.id(), result.content().substring(0, Math.min(20, result.content().length())));

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID commentId,
        @RequestHeader("Monew-Request-User-ID") UUID userId
    ) {
        log.info("Start - CommentController/delete: commentId={}, userId={}",
            commentId, userId);

        commentService.delete(commentId, userId);

        log.info("Complete - CommentController/delete: commentId={}", commentId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}/hard")
    public ResponseEntity<Void> hardDelete(
        @PathVariable UUID commentId
    ) {
        log.info("Start - CommentController/hardDelete: commentId={}", commentId);

        commentService.hardDelete(commentId);

        log.info("Complete - CommentController/hardDelete: commentId={}", commentId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public ResponseEntity<CursorPageResponseCommentDto> findAll(
        @RequestHeader("MoNew-Request-User-ID") UUID userId, @Valid @ModelAttribute
    CursorPageRequestCommentDto cursorPageRequestCommentDto) {
        log.info("Start - CommentController/findAll");
        CursorPageResponseCommentDto result = commentService.findAll(userId,
            cursorPageRequestCommentDto);
        log.info("Complete - CommentController/findAll");
        return ResponseEntity.ok(result);
    }

}
