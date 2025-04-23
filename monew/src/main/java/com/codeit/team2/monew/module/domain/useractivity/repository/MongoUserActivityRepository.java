package com.codeit.team2.monew.module.domain.useractivity.repository;

import com.codeit.team2.monew.module.domain.useractivity.document.UserActivity;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUserActivityRepository extends MongoRepository<UserActivity, UUID> {

}
