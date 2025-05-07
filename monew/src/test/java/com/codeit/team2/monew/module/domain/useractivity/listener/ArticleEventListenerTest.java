package com.codeit.team2.monew.module.domain.useractivity.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.article.event.ArticleViewCreateEvent;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.useractivity.service.MongoUserActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArticleEventListenerTest {

    @Mock
    private MongoUserActivityService userActivityService;

    @InjectMocks
    private ArticleEventListener listener;

    @Test
    void createArticleViewItem_호출_성공() {
        // given
        ArticleView articleView = mock(ArticleView.class);
        User user = mock(User.class);
        ArticleViewCreateEvent event = new ArticleViewCreateEvent(articleView, user);

        // when
        listener.createArticleViewItem(event);

        // then
        verify(userActivityService).createArticleViewItem(articleView, user);
    }
}

