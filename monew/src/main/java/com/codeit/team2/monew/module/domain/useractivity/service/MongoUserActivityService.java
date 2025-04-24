package com.codeit.team2.monew.module.domain.useractivity.service;

import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.useractivity.document.UserActivity;
import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import com.codeit.team2.monew.module.domain.useractivity.mapper.UserActivityMapper;
import com.codeit.team2.monew.module.domain.useractivity.repository.MongoUserActivityRepository;
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
    private final UserActivityMapper userActivityMapper;

    @Override
    public UserActivityDto findUserActivities(UUID loginId, UUID userId) {
        if (!loginId.equals(userId)) {
            throw new RuntimeException("Not Authorized");
        }

        UserActivity userActivity = userActivityRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Not Found UserActivity"));

        return userActivityMapper.toUserActivityDto(userActivity);
    }

    public void createUserActivity(User user) {
        userActivityRepository.save(
            new UserActivity(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
            )
        );
    }
}
