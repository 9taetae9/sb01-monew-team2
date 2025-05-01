package com.codeit.team2.monew.module.domain.comment.exception;

import com.codeit.team2.monew.module.common.exception.BaseException;
import java.util.Map;
import java.util.UUID;

public class CommentPermissionDeniedException extends BaseException {

    public CommentPermissionDeniedException(UUID commentId, UUID userId) {
        super(CommentErrorCode.COMMENT_PERMISSION_DENIED,
            Map.of("commentId", commentId, "userId", userId));

    }
}
