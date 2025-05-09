package com.codeit.team2.monew.module.domain.interest.repository;

import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends JpaRepository<Interest, UUID>,
    InterestCustomRepository {

    @Query(value = "SELECT EXISTS (SELECT 1 From interests WHERE word_similarity(name, :searchName) >= 0.6)",
        nativeQuery = true)
    boolean existsByNameSimilarTo(@Param("searchName") String searchName);

    @Query(value = "SELECT similarity(:a, :b)", nativeQuery = true)
    Double getSimilarity(@Param("a") String a, @Param("b") String b);

    @Query("SELECT i.name FROM Interest i")
    List<String> findAllNames();

    @Query("SELECT DISTINCT i FROM Interest i "
        + "JOIN FETCH i.keywords ik "
        + "JOIN FETCH ik.keyword "
        + "WHERE i.id = :id")
    Optional<Interest> findByIdWithKeywords(@Param("id") UUID id);
}
