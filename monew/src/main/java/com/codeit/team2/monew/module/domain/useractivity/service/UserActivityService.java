package com.codeit.team2.monew.module.domain.useractivity.service;

import com.codeit.team2.monew.module.domain.useractivity.dto.UserActivityDto;
import java.util.UUID;

public interface UserActivityService {

    UserActivityDto findUserActivities(UUID loginId, UUID userId);
}
