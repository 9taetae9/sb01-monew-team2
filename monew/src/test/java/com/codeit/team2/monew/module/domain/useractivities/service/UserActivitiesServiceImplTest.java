package com.codeit.team2.monew.module.domain.useractivities.service;

import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserActivitiesServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleViewRepository articleViewRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Spy
    private UserActivitiesMapper userActivitiesMapper = Mappers.getMapper(
        UserActivitiesMapper.class);

    @InjectMocks
    private UserActivitiesServiceImpl userActivitiesService;

    @Test
    void 사용자_활동_내역_조회_성공() {
        // given

        // when
        UserActivityDto userActivityDto = userActivitiesService.findUserActivities(loginId, userId);

        // then

    }

}
