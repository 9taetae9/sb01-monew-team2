package com.codeit.team2.monew.module.domain.interest.repository;

import com.codeit.team2.monew.module.domain.interest.entity.InterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestKeywordRepository extends JpaRepository<InterestKeyword, UUID> {


    @Query("""
        SELECT ik
        FROM InterestKeyword ik
        JOIN FETCH ik.interest
        WHERE ik.keyword = :keyword
        """)
    List<InterestKeyword> findAllByKeyword(Keyword keyword);

    boolean existsByKeyword(Keyword keyword);

    Optional<InterestKeyword> findTopByKeywordOrderByCreatedAtDesc(Keyword keyword);

}
