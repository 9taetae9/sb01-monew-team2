package com.codeit.team2.monew.module.domain.interest.repository;

import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface KeywordRepository extends JpaRepository<Keyword, UUID> {

    Optional<Keyword> findByName(String name);

    @Modifying
    @Query("DELETE FROM Keyword k "
        + "WHERE NOT EXISTS ("
        + "SELECT 1 FROM InterestKeyword ik WHERE ik.keyword = k"
        + ")"
    )
    void deleteAllOrphanKeywords();
}
