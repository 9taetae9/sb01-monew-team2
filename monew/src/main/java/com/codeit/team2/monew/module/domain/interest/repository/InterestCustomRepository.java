package com.codeit.team2.monew.module.domain.interest.repository;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestOrderBy;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import java.time.Instant;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;

public interface InterestCustomRepository {

    Slice<Interest> findAll(String keyword, InterestOrderBy orderBy, Direction direction,
        String cursor,
        Instant after, int limit);

    long countFilteredTotalElements(String keyword, InterestOrderBy orderBy, Direction direction);
}
