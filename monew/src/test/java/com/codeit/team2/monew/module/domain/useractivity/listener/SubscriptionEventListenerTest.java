package com.codeit.team2.monew.module.domain.useractivity.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.subscription.event.SubscriptionRegisterEvent;
import com.codeit.team2.monew.module.domain.useractivity.service.MongoUserActivityService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriptionEventListenerTest {

    @Mock
    private MongoUserActivityService userActivityService;

    @InjectMocks
    private SubscriptionEventListener listener;

    @Test
    void createSubscriptionItem_호출_성공() {
        // given
        Subscription subscription = mock(Subscription.class);
        Interest interest = mock(Interest.class);
        UUID userId = UUID.randomUUID();
        SubscriptionRegisterEvent event = new SubscriptionRegisterEvent(subscription, interest,
            userId);

        // when
        listener.createSubscriptionItem(event);

        // then
        verify(userActivityService).createSubscriptionItem(subscription, interest, userId);
    }

}
