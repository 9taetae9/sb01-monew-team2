package com.codeit.team2.monew.module.domain.useractivity.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.event.UserRegisterEvent;
import com.codeit.team2.monew.module.domain.useractivity.service.MongoUserActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserEventListenerTest {

    @Mock
    private MongoUserActivityService userActivityService;

    @InjectMocks
    private UserEventListener listener;

    @Test
    void createUserActivity_호출_성공() {
        // given
        User user = mock(User.class);
        UserRegisterEvent event = new UserRegisterEvent(user);

        // when
        listener.createUserActivity(event);

        // then
        verify(userActivityService).createUserActivity(user);
    }
}
