package com.codeit.team2.monew.module.domain.useractivity.listener;

import com.codeit.team2.monew.module.domain.interest.event.InterestDeleteEvent;
import com.codeit.team2.monew.module.domain.useractivity.service.MongoUserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.db-type", havingValue = "mongodb")
public class InterestEventListener {

    private final MongoUserActivityService userActivityService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteSubscriptionItem(InterestDeleteEvent event) {
        userActivityService.deleteSubscriptionItem(
            event.interest(),
            event.userId()
        );
    }

}
