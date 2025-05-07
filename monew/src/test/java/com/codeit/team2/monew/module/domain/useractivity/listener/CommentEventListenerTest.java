package com.codeit.team2.monew.module.domain.useractivity.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.event.CommentRegisterEvent;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.useractivity.service.MongoUserActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentEventListenerTest {

    @Mock
    private MongoUserActivityService userActivityService;

    @InjectMocks
    private CommentEventListener listener;

    @Test
    void createCommentItem_호출_성공() {
        // given
        Comment comment = mock(Comment.class);
        Article article = mock(Article.class);
        User user = mock(User.class);
        CommentRegisterEvent event = new CommentRegisterEvent(comment, article, user);

        // when
        listener.createCommentItem(event);

        // then
        verify(userActivityService).createCommentItem(comment, article, user);
    }

}
