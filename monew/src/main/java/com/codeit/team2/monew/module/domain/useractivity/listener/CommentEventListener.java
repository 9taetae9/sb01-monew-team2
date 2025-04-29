package com.codeit.team2.monew.module.domain.useractivity.listener;

import com.codeit.team2.monew.module.domain.comment.event.CommentLikeDeleteEvent;
import com.codeit.team2.monew.module.domain.comment.event.CommentLikeRegisterEvent;
import com.codeit.team2.monew.module.domain.comment.event.CommentRegisterEvent;
import com.codeit.team2.monew.module.domain.comment.event.CommentUpdateEvent;
import com.codeit.team2.monew.module.domain.useractivity.service.MongoUserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.db-type", havingValue = "mongodb")
public class CommentEventListener {

    private final MongoUserActivityService userActivityService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createCommentItem(CommentRegisterEvent event) {
        userActivityService.createCommentItem(
            event.comment(),
            event.article(),
            event.user()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createCommentLikeItem(CommentLikeRegisterEvent event) {
        userActivityService.createCommentLikeItem(
            event.commentLike()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateCommentItem(CommentUpdateEvent event) {
        userActivityService.updateCommentContentInActivity(
            event.comment(),
            event.userId()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteCommentLikeItem(CommentLikeDeleteEvent event) {
        userActivityService.deleteCommentLikeItem(
            event.commentLike()
        );
    }
}
