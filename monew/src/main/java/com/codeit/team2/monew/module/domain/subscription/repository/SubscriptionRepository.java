package com.codeit.team2.monew.module.domain.subscription.repository;

import com.codeit.team2.monew.module.domain.subscription.entity.Subscription;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

}
