package com.codeit.team2.monew.module.domain.subscription.repository;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<SubscriptionRepository, UUID> {

    List<Subscription> findAllByInterest(Interest interest);

}
