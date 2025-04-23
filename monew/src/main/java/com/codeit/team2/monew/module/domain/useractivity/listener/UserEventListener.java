package com.codeit.team2.monew.module.domain.useractivity.listener;

import com.codeit.team2.monew.module.domain.user.event.RegisterUserEvent;
import com.codeit.team2.monew.module.domain.useractivity.service.MongoUserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final MongoUserActivityService userActivityService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createUserActivity(RegisterUserEvent event) {
        userActivityService.createUserActivity(event.user());
    }

}
