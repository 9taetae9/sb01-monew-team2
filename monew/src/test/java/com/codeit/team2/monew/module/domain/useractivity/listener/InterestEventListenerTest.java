package com.codeit.team2.monew.module.domain.useractivity.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.event.InterestDeleteEvent;
import com.codeit.team2.monew.module.domain.interest.event.InterestUpdateEvent;
import com.codeit.team2.monew.module.domain.useractivity.service.MongoUserActivityService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InterestEventListenerTest {

    @Mock
    private MongoUserActivityService userActivityService;

    @InjectMocks
    private InterestEventListener listener;

    @Test
    void updateSubscriptionItem_호출_성공() {
        // given
        Interest interest = mock(Interest.class);
        List<String> keywords = new ArrayList<>();
        UUID userId = UUID.randomUUID();
        InterestUpdateEvent event = new InterestUpdateEvent(interest, keywords, userId);

        // when
        listener.updateSubscriptionItem(event);

        // then
        verify(userActivityService).updateSubscriptionItemInActivity(interest, keywords, userId);
    }

    @Test
    void deleteSubscriptionItem_호출_성공() {
        // given
        Interest interest = mock(Interest.class);
        UUID userId = UUID.randomUUID();
        InterestDeleteEvent event = new InterestDeleteEvent(interest, userId);

        // when
        listener.deleteSubscriptionItem(event);

        // then
        verify(userActivityService).deleteSubscriptionItem(interest, userId);
    }
}
