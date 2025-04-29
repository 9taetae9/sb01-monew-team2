package com.codeit.team2.monew.module.domain.notification.repository;

import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import com.codeit.team2.monew.module.domain.notification.entity.QNotification;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Notification> findWithCursor(UUID userId, Instant cursor, UUID after, int limit) {
        QNotification notification = QNotification.notification;

        // 확인하지 않은 알림 & 특정 유저의 알림
        BooleanBuilder where = new BooleanBuilder()
            .and(notification.user.id.eq(userId))
            .and(notification.confirmed.isFalse());
        // 커서 조건
        if (cursor != null && after != null) {
            where.and(notification.createdAt.gt(cursor)
                .or((notification.createdAt.eq(cursor)).and(notification.id.gt(after))));
        }

        List<Notification> result = queryFactory.select(notification)
            .where(where)
            .orderBy(notification.createdAt.asc(), notification.id.asc())
            .limit(limit + 1)
            .fetch();

        boolean hasNext = result.size() > limit;
        if (hasNext) {
            result.remove(limit);
        }

        return new SliceImpl<>(result, PageRequest.of(0, limit), hasNext);
    }
}
