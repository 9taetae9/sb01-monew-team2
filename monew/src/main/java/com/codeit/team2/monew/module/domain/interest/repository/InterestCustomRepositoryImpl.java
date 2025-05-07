package com.codeit.team2.monew.module.domain.interest.repository;

import com.codeit.team2.monew.module.domain.interest.dto.request.InterestOrderBy;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.interest.entity.QInterest;
import com.codeit.team2.monew.module.domain.interest.entity.QInterestKeyword;
import com.codeit.team2.monew.module.domain.interest.entity.QKeyword;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InterestCustomRepositoryImpl implements InterestCustomRepository {

    private final JPAQueryFactory queryFactory;

    private BooleanBuilder buildCommonFilters(String keyword) {
        QInterest interest = QInterest.interest;
        QKeyword keywordEntity = QKeyword.keyword;

        // 검색 조건: Interest.name or Keyword.name 부분일치
        BooleanBuilder where = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            where.and(
                interest.name.containsIgnoreCase(keyword)
                    .or(keywordEntity.name.containsIgnoreCase(keyword))
            );
        }
        return where;
    }

    @Override
    public long countFilteredTotalElements(String keyword, InterestOrderBy orderBy,
        Direction direction) {
        QInterest interest = QInterest.interest;
        QInterestKeyword interestKeyword = QInterestKeyword.interestKeyword;
        QKeyword keywordEntity = QKeyword.keyword;

        JPAQuery<Long> query = queryFactory
            .select(interest.countDistinct())
            .from(interest);

        // keyword가 있을 때만 조인
        if (keyword != null && !keyword.isBlank()) {
            query
                .leftJoin(interest.keywords, interestKeyword)
                .leftJoin(interestKeyword.keyword, keywordEntity);
        }

        query.where(buildCommonFilters(keyword));

        return query.fetchOne();
    }


    @Override
    public Slice<Interest> findAll(String keyword, InterestOrderBy orderBy, Direction direction,
        String cursor, Instant after, int limit) {
        QInterest interest = QInterest.interest;
        QInterestKeyword interestKeyword = QInterestKeyword.interestKeyword;
        QKeyword keywordEntity = QKeyword.keyword;

        JPAQuery<Interest> query = queryFactory
            .selectFrom(interest)
            .distinct()
            .leftJoin(interest.keywords, interestKeyword).fetchJoin()
            .leftJoin(interestKeyword.keyword, keywordEntity).fetchJoin();

        BooleanBuilder where = buildCommonFilters(keyword);

        // 커서 조건
        if (cursor != null) {
            // name 기준 정렬
            if (orderBy == InterestOrderBy.name) {
                if (direction == Direction.ASC) {
                    where.and(interest.name.gt(cursor));
                } else {
                    where.and(interest.name.lt(cursor));
                }
                // subscriberCount 기준 정렬
            } else if (orderBy == InterestOrderBy.subscriberCount) {
                Long countCursor = Long.parseLong(cursor);

                if (direction == Direction.ASC) {
                    where.and(
                        interest.subscriberCount.gt(countCursor)
                            .or(interest.subscriberCount.eq(countCursor)
                                .and(interest.createdAt.gt(after)))
                    );
                } else {
                    where.and(
                        interest.subscriberCount.lt(countCursor)
                            .or(interest.subscriberCount.eq(countCursor)
                                .and(interest.createdAt.lt(after)))
                    );
                }
            }
        }

        query.where(where);

        // 정렬 조건
        OrderSpecifier<?> orderSpecifier;
        OrderSpecifier<?> createdAtOrderSpecifier = interest.createdAt.asc(); // 보조 정렬 기준

        if (orderBy == InterestOrderBy.name) {
            orderSpecifier = direction == Direction.ASC
                ? interest.name.asc()
                : interest.name.desc();
        } else {
            orderSpecifier = direction == Direction.ASC
                ? interest.subscriberCount.asc()
                : interest.subscriberCount.desc();

            createdAtOrderSpecifier = direction == Direction.ASC
                ? interest.createdAt.asc()
                : interest.createdAt.desc();
        }

        query.orderBy(orderSpecifier, createdAtOrderSpecifier);

        // hasNext 확인하기 위해 1개 더 가져옴
        query.limit(limit + 1);

        List<Interest> results = query.fetch();

        boolean hasNext = results.size() > limit;
        if (hasNext) {
            results.remove(limit);
        }

        return new SliceImpl<>(results, PageRequest.of(0, limit), hasNext);
    }
}
