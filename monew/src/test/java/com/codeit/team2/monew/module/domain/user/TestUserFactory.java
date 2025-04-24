package com.codeit.team2.monew.module.domain.user;

import com.codeit.team2.monew.module.domain.user.entity.User;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class TestUserFactory {

    public static User createWithName(String name) {
        User user = new User(
            "email@mail.com",
            "name",
            "pw",
            false);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        return user;
    }

}
