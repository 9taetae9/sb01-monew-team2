package com.codeit.team2.monew.module.domain.useractivity.service;

import com.codeit.team2.monew.module.domain.article.repository.ArticleViewRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentLikeRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.subscription.repository.SubscriptionRepository;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.mapper.UserActivityMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserActivityServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private ArticleViewRepository articleViewRepository;

    @Spy
    private UserActivityMapper userActivitiesMapper = Mappers.getMapper(
        UserActivityMapper.class);

    @InjectMocks
    private UserActivityServiceImpl userActivitiesService;

    @Test
    void 사용자_활동_내역_조회_성공() {
        // given
        UUID userId = UUID.randomUUID();
        UUID loginId = userId;

        // when
        UserActivityDto userActivityDto = userActivitiesService.findUserActivities(loginId, userId);

        // then

    }

}
