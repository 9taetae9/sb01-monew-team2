package com.codeit.team2.monew.module.domain.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.config.JpaConfig;
import com.codeit.team2.monew.config.QuerydslConfig;
import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.ResourceType;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test-temp")
@Import({JpaConfig.class, QuerydslConfig.class})
@Transactional
class NotificationCustomRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Qualifier("notificationCustomRepositoryImpl")
    @Autowired
    private NotificationCustomRepository notificationCustomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    private UUID userId;

    @Test
    void testFindWithCursor_정상작동() {
        // given: 유저 및 알림 데이터 세팅
        User user = userRepository.save(new User("email", "nick", "pw", false));
        userId = user.getId();

        for (int i = 0; i < 5; i++) {
            Notification n = new Notification(user, "알림" + i, false, UUID.randomUUID(),
                ResourceType.INTEREST);
            ReflectionTestUtils.setField(n, "createdAt",
                Instant.now().minusSeconds(1000 - i * 100));
            notificationRepository.save(n);
        }
        em.flush();
        em.clear();

        Instant cursor = Instant.now().minusSeconds(850); // 2번째 알림보다 이후
        Instant after = cursor; // 동일하게 넣음
        int limit = 2;

        // when
        Slice<Notification> result = notificationCustomRepository.findWithCursor(userId, cursor,
            after, limit);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent().get(0).getContent()).isEqualTo("알림0");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("알림1");
    }

    @Test
    void testFindWithCursor_확인된알림제외() {
        // given
        User user = userRepository.save(new User("email", "nick", "pw", false));
        userId = user.getId();

        // 확인 안 된 알림
        Notification n1 = new Notification(user, "미확인", false, UUID.randomUUID(),
            ResourceType.INTEREST);
        ReflectionTestUtils.setField(n1, "createdAt", Instant.now().minusSeconds(1000));
        notificationRepository.save(n1);

        // 확인된 알림
        Notification n2 = new Notification(user, "확인됨", true, UUID.randomUUID(),
            ResourceType.INTEREST);
        ReflectionTestUtils.setField(n2, "createdAt", Instant.now().minusSeconds(900));
        notificationRepository.save(n2);

        em.flush();
        em.clear();

        // when
        Slice<Notification> result = notificationCustomRepository.findWithCursor(userId,
            Instant.EPOCH, Instant.EPOCH, 10);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("미확인");
    }
}
