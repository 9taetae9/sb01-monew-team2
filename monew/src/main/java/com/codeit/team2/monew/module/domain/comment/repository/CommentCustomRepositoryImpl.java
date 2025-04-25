package com.codeit.team2.monew.module.domain.comment.repository;

import com.codeit.team2.monew.module.domain.comment.dto.CommentOrderBy;
import com.codeit.team2.monew.module.domain.comment.entity.Comment;
import com.codeit.team2.monew.module.domain.comment.entity.QComment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Comment> findAll(UUID articleId, CommentOrderBy orderBy, Direction direction,
        Object cursor, Instant after, int limit) {
        QComment comment = QComment.comment;

        // 특정 article의 삭제되지 않은 댓글만
        BooleanBuilder where = new BooleanBuilder()
            .and(comment.article.id.eq(articleId))
            .and(comment.deleted.isFalse());

        // 커서 조건
        if (cursor != null) {
            System.out.println("cursor: " + cursor);
            // 날짜 기준 정렬
            if (orderBy.equals(CommentOrderBy.createdAt)) {
                // Object -> Instant
                Instant createdAtCursor = Instant.parse((String) cursor);
                if (direction.equals(Direction.ASC)) {
                    // ASC
                    where.and(comment.createdAt.gt(createdAtCursor));
                } else {
                    // DESC
                    where.and(comment.createdAt.lt(createdAtCursor));
                }
            } // 좋아요 수 기준 정렬
            else if (orderBy.equals(CommentOrderBy.likeCount)) {
                Long likeCountCursor = Long.parseLong((String) cursor);
                if (direction.equals(Direction.ASC)) {
                    // ASC
                    where.and(comment.likeCount.gt(likeCountCursor)
                        .or(comment.likeCount.eq(likeCountCursor)
                            .and(comment.createdAt.gt(after))));
                } else {
                    // DESC
                    where.and(comment.likeCount.lt(likeCountCursor))
                        .or(comment.likeCount.eq(likeCountCursor)
                            .and(comment.createdAt.lt(after)));
                }
            }
        }

        // 정렬하기
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        if (orderBy == CommentOrderBy.createdAt) {
            orderSpecifiers.add(
                direction == Direction.ASC
                    ? comment.createdAt.asc()
                    : comment.createdAt.desc()
            );
        } else if (orderBy == CommentOrderBy.likeCount) {
            orderSpecifiers.add(
                direction == Direction.ASC
                    ? comment.likeCount.asc()
                    : comment.likeCount.desc()
            );
            orderSpecifiers.add(
                direction == Direction.ASC
                    ? comment.createdAt.asc()
                    : comment.createdAt.desc()
            );
        }

        List<Comment> result = queryFactory
            .selectFrom(comment)
            .where(where)
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .limit(limit + 1)
            .fetch();

        boolean hasNext = result.size() > limit;
        if (hasNext) {
            result.remove(limit);
        }

        return new SliceImpl<>(result, PageRequest.of(0, limit), hasNext);
    }
}
