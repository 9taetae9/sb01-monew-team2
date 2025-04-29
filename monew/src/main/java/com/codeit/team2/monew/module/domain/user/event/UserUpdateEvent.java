package com.codeit.team2.monew.module.domain.user.event;

import com.codeit.team2.monew.module.domain.user.entity.User;

public record UserUpdateEvent(
    User user
) {

}
