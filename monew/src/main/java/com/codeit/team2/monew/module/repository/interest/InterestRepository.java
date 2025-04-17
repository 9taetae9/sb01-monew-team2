package com.codeit.team2.monew.module.repository.interest;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends JpaRepository<Interest, UUID> {

}
