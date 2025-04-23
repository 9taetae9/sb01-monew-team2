package com.codeit.team2.monew.module.domain.useractivity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.useractivity.document.UserActivity;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.mapper.UserActivityMapper;
import com.codeit.team2.monew.module.domain.useractivity.repository.MongoUserActivityRepository;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MongoUserActivityServiceTest {

    @Mock
    private MongoUserActivityRepository userActivityRepository;

    @Spy
    private UserActivityMapper userActivitiesMapper = Mappers.getMapper(
        UserActivityMapper.class);

    @InjectMocks
    private MongoUserActivityService userActivityService;

    @Nested
    class findUserActivitiesTest {

        @Test
        void 활동_내역_관리_조회_성공() {
            // given
            UUID userId = UUID.randomUUID();
            UUID loginId = userId;

            String email = "email";
            String nickname = "nickname";
            Instant createdAt = Instant.now();

            UserActivity userActivity = new UserActivity(
                userId,
                email,
                nickname,
                createdAt,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
            );

            when(userActivityRepository.findById(userId)).thenReturn(Optional.of(userActivity));

            // when
            UserActivityDto userActivityDto =
                userActivityService.findUserActivities(loginId, userId);

            // then
            assertEquals(userId, userActivityDto.id());
            assertEquals(email, userActivityDto.email());
            assertEquals(nickname, userActivityDto.nickname());
            assertEquals(createdAt, userActivityDto.createdAt());
        }
    }

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
