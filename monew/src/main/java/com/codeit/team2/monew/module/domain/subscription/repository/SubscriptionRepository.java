package com.codeit.team2.monew.module.domain.subscription.repository;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import com.codeit.team2.monew.module.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findAllByUserOrderByCreatedAtDesc(User user);

    List<Subscription> findAllByInterest(Interest interest);

    boolean existsByInterestAndUser(Interest interest, User user);

    Optional<Subscription> findByInterestAndUser(Interest interest, User user);


    @Query("SELECT s.interest.id FROM Subscription s " +
        "WHERE s.user.id = :userId AND s.interest.id IN :interestIds")
    Set<UUID> findSubscribedInterestIds(@Param("userId") UUID userId,
        @Param("interestIds") Set<UUID> interestIds);


    void deleteByInterestAndUser(Interest interest, User user);

}


