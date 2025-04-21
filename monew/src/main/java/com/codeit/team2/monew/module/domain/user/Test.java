package com.codeit.team2.monew.module.domain.user;

import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.notification.repository.NotificationRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

// hard delete 정상 작동 확인 위한 테스트 클래스
//@Component
@RequiredArgsConstructor
public class Test {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;


    @PostConstruct
    void init() {
        User user = userRepository.save(
            new User("email@a.com", "nickname", "passowrd", false)
        );

        Notification notification = notificationRepository.save(
            new Notification(user, "content", false, UUID.randomUUID(), ResourceType.COMMENT)
        );
    }

}
