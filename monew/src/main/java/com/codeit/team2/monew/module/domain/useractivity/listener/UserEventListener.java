package com.codeit.team2.monew.module.domain.useractivity.listener;

import com.codeit.team2.monew.module.domain.user.event.RegisterUserEvent;
import com.codeit.team2.monew.module.domain.useractivity.service.MongoUserActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final MongoUserActivityService userActivityService;

    @EventListener
    public void createUserActivity(RegisterUserEvent event) {
        userActivityService.createUserActivity(event.user());
    }

}
