package com.codeit.team2.monew.module.domain.useractivity.service;

import com.codeit.team2.monew.module.domain.useractivity.document.UserActivity;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.repository.MongoUserActivityRepository;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.db-type", havingValue = "mongodb")
public class MongoUserActivityService implements UserActivityService {

    private final MongoUserActivityRepository userActivityRepository;

    @Override
    public UserActivityDto findUserActivities(UUID loginId, UUID userId) {
        return null;
    }

    @PostConstruct
    void init() {
        userActivityRepository.save(
            new UserActivity(
                UUID.randomUUID(),
                "email",
                "nickname",
                Instant.now(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
            )
        );
    }
}
