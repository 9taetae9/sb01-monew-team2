package com.codeit.team2.monew.module.domain.notification.repository;

import com.codeit.team2.monew.module.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Modifying
    @Query("UPDATE Notification n SET n.confirmed = true WHERE n.user.id = :userId AND n.confirmed = false")
    int confirmAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.confirmed = true AND n.createdAt < :time")
    int deleteByConfirmedIsTrueAndCreatedAtBefore(@Param("time") Instant time);

    @Query("SELECT COUNT(n) FROM Notification  n WHERE n.user.id"
        + "=:userId AND n.confirmed = false ")
    long countForPagination(@Param("userId") UUID userId);
}
