package com.codeit.team2.monew.module.domain.useractivity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.useractivity.document.UserActivity;
import com.codeit.team2.monew.module.domain.useractivity.repository.MongoUserActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MongoUserActivityServiceTest {

    @Mock
    private MongoUserActivityRepository userActivityRepository;

    @InjectMocks
    private MongoUserActivityService userActivityService;

    @Test
    void 사용자_활동_내역_초기_생성_성공() {
        // given
        String email = "email";
        String nickname = "nickname";
        String password = "password";

        User user = new User(email, nickname, password, false);

        // when
        userActivityService.createUserActivity(user);

        // then
        ArgumentCaptor<UserActivity> captor = ArgumentCaptor.forClass(UserActivity.class);
        verify(userActivityRepository).save(captor.capture());

        UserActivity saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(saved.getNickname()).isEqualTo(nickname);
        assertThat(saved.getSubscriptions()).isEmpty();
        assertThat(saved.getComments()).isEmpty();
        assertThat(saved.getCommentLikes()).isEmpty();
        assertThat(saved.getArticleViews()).isEmpty();
    }

}
