package com.codeit.team2.monew.module.domain.notification.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.config.QuerydslConfig;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test-temp")
@Import({JpaConfig.class, QuerydslConfig.class})
@Transactional
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    private User setupUserWithNotifications() {
        User user = userRepository.save(new User("email", "name", "password", false));
        em.flush();
        em.clear();

        User managedUser = em.find(User.class, user.getId());

        for (int i = 0; i < 2; i++) {
            notificationRepository.save(
                new Notification(managedUser, "알림" + i, false, UUID.randomUUID(),
                    ResourceType.INTEREST));
        }
        Notification oldNotification = new Notification(managedUser, "확인된 알림", true,
            UUID.randomUUID(),
            ResourceType.INTEREST);
        ReflectionTestUtils.setField(oldNotification, "createdAt",
            Instant.now().minus(7, ChronoUnit.DAYS));
        notificationRepository.save(oldNotification);

        return managedUser;
    }

    @Test
    void testConfirmAllByUserId() {
        User user = setupUserWithNotifications();
        int updatedCount = notificationRepository.confirmAllByUserId(user.getId());
        assertThat(updatedCount).isEqualTo(2);

        long remainingUnconfirmed = notificationRepository.countForPagination(user.getId());
        assertThat(remainingUnconfirmed).isEqualTo(0);
    }

    @Test
    void testDeleteByConfirmedIsTrueAndCreatedAtBefore() {
        User user = setupUserWithNotifications();

        Instant now = Instant.now();
        int deleted = notificationRepository.deleteByConfirmedIsTrueAndCreatedAtBefore(
            now);
        assertThat(deleted).isEqualTo(1);
    }

    @Test
    void testCountForPagination() {
        User user = setupUserWithNotifications();

        long count = notificationRepository.countForPagination(user.getId());
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testFindWithCursor() {
        User user = setupUserWithNotifications();

        Instant cursor = null;
        Instant after = null;
        int limit = 10;

        Slice<Notification> result = notificationRepository.findWithCursor(user.getId(), cursor,
            after, limit);
        assertThat(result.getContent().size()).isEqualTo(2);  // 명시적 표현
    }
}
