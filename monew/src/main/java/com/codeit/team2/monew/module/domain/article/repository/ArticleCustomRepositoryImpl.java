package com.codeit.team2.monew.module.domain.article.repository;

import com.codeit.team2.monew.module.domain.article.dto.request.ArticleOrderBy;
import com.codeit.team2.monew.module.domain.article.dto.request.ArticleSourceIn;
import com.codeit.team2.monew.module.domain.article.dto.request.CursorPageRequestArticleDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.QArticle;
import com.codeit.team2.monew.module.domain.comment.entity.QComment;
import com.codeit.team2.monew.module.domain.relation.entity.QArticleInterest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
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
public class ArticleCustomRepositoryImpl implements ArticleCustomRepository {

    private final JPAQueryFactory queryFactory;

    // 필터링 조건 where절 구하기
    private BooleanBuilder buildCommonFilters(QArticle article, QArticleInterest articleInterest,
        String keyword, UUID interestId, List<ArticleSourceIn> sourceIn,
        Instant publishDateFrom, Instant publishDateTo) {

        BooleanBuilder where = new BooleanBuilder();

        // 삭제되지 않은 기사만
        where.and(article.deleted.isFalse());

        // keyword가 제목, 요약과 부분일치하는 경우
        if (keyword != null && !keyword.isBlank()) {
            where.and(article.title.contains(keyword)
                .or(article.summary.contains(keyword)));
        }

        // 관심사 필터링(ArticleInterest와 join 필요)
        if (interestId != null) {
            where.and(article.articleInterests.any().interest.id.eq(interestId));
        }

        // 출처 필터링
        if (sourceIn != null && !sourceIn.isEmpty()) {
            List<String> sources = sourceIn.stream().map(Enum::name).toList();
            where.and(article.source.in(sources));
        }

        // 날짜 범위 필터링
        if (publishDateFrom != null) {
            where.and(article.publishedDate.goe(publishDateFrom));
        }
        if (publishDateTo != null) {
            where.and(article.publishedDate.loe(publishDateTo));
        }

        return where;
    }

    // 정렬 기준 & 방향에 따른 커서 where절 구하기
    private BooleanBuilder buildCursor(QArticle article, QComment comment,
        ArticleOrderBy orderBy, Direction direction,
        String cursor, Instant after) {
        BooleanBuilder where = new BooleanBuilder();

        switch (orderBy) {
            case publishDate -> {
                Instant publishDateCursor = Instant.parse(cursor);
                if (direction.isAscending()) {
                    where.and(article.publishedDate.gt(publishDateCursor))
                        .or(article.publishedDate.eq(publishDateCursor)
                            .and(article.createdAt.gt(after)));
                } else {
                    where.and(article.publishedDate.lt(publishDateCursor))
                        .or(article.publishedDate.eq(publishDateCursor)
                            .and(article.createdAt.lt(after)));
                }
            }
            case viewCount -> {
                Long viewCountCursor = Long.parseLong(cursor);
                if (direction.isAscending()) {
                    where.and(article.viewCount.gt(viewCountCursor))
                        .or(article.viewCount.eq(viewCountCursor)
                            .and(article.createdAt.gt(after)));
                } else {
                    where.and(article.viewCount.lt(viewCountCursor))
                        .or(article.viewCount.eq(viewCountCursor)
                            .and(article.createdAt.lt(after)));
                }
            }
            default -> throw new IllegalArgumentException("Unsupported orderBy");
        }

        return where;
    }

    // commentCount 정렬하는 경우 having절 추가
    private BooleanExpression buildCursorHaving(QArticle article, QComment comment,
        Direction direction, String cursor, Instant after) {
        Long commentCountCursor = Long.parseLong(cursor);

        if (direction.isAscending()) {
            return comment.count().gt(commentCountCursor)
                .or(comment.count().eq(commentCountCursor)
                    .and(article.createdAt.gt(after)));
        } else {
            return comment.count().lt(commentCountCursor)
                .or(comment.count().eq(commentCountCursor)
                    .and(article.createdAt.lt(after)));
        }
    }

    // ORDER BY 지정하기
    private List<OrderSpecifier<?>> buildOrderSpecifiers(QArticle article, QComment comment,
        ArticleOrderBy orderBy, Direction direction) {

        OrderSpecifier<?> order = switch (orderBy) {
            case publishDate -> direction.isAscending() ? article.publishedDate.asc()
                : article.publishedDate.desc();
            case viewCount ->
                direction.isAscending() ? article.viewCount.asc() : article.viewCount.desc();
            case commentCount ->
                direction.isAscending() ? comment.count().asc() : comment.count().desc();
        };

        // 보조 커서 createdAt 정렬
        OrderSpecifier<?> createdAtOrder = direction.isAscending()
            ? article.createdAt.asc()
            : article.createdAt.desc();

        return List.of(order, createdAtOrder);
    }


    @Override
    public Slice<Article> findWithCursor(CursorPageRequestArticleDto request) {
        String keyword = request.keyword();
        UUID interestId = request.interestId();
        List<ArticleSourceIn> sourceIn = request.sourceIn();
        Instant publishDateFrom = request.getPublishDateFromInstant();
        Instant publishDateTo = request.getPublishDateToInstant();
        ArticleOrderBy orderBy = request.orderBy();
        Direction direction = request.direction();
        String cursor = request.cursor();
        Instant after = request.after();
        int limit = request.limit();

        QArticle article = QArticle.article;
        QArticleInterest articleInterest = QArticleInterest.articleInterest;
        QComment comment = QComment.comment;

        // 기본 쿼리: Article에서 select
        JPAQuery<Article> query = queryFactory.selectFrom(article);
        // commentCount 정렬이면 comment join 추가
        if (orderBy.equals(ArticleOrderBy.commentCount)) {
            query.leftJoin(comment).on(comment.article.eq(article));
        }
        // 관심사 조건이 있으면 article interest join 추가
        if (interestId != null) {
            query.leftJoin(article.articleInterests, articleInterest);
        }

        // 검색 조건
        BooleanBuilder where = buildCommonFilters(article, articleInterest, keyword, interestId,
            sourceIn, publishDateFrom, publishDateTo);

        // 커서가 있는 경우 where 추가
        if (cursor != null && after != null) {
            if (orderBy.equals(ArticleOrderBy.commentCount)) {
                BooleanExpression cursorCondition = buildCursorHaving(article, comment, direction,
                    cursor, after);
                query.having(cursorCondition);
            } else {
                BooleanBuilder cursorCondition = buildCursor(article, comment, orderBy, direction,
                    cursor, after);
                where.and(cursorCondition);
            }
        }

        query.where(where);

        // 정렬 조건
        List<OrderSpecifier<?>> orderSpecifiers = buildOrderSpecifiers(article, comment, orderBy,
            direction);
        for (OrderSpecifier<?> os : orderSpecifiers) {
            query.orderBy(os);
        }

        if (orderBy.equals(ArticleOrderBy.commentCount)) {
            query.groupBy(article.id);
        }

        query.limit(limit + 1);

        List<Article> result = query.fetch();

        boolean hasNext = result.size() > limit;
        if (hasNext) {
            result.remove(limit);
        }

        return new SliceImpl<>(result, PageRequest.of(0, limit), hasNext);
    }

    @Override
    public long countFilteredTotalElements(String keyword, UUID interestId,
        List<ArticleSourceIn> sourceIn, Instant publishDateFrom, Instant publishDateTo) {
        QArticle article = QArticle.article;
        QArticleInterest articleInterest = QArticleInterest.articleInterest;

        JPAQuery<Article> query = queryFactory.selectFrom(article);
        BooleanBuilder where = buildCommonFilters(article, articleInterest,
            keyword, interestId, sourceIn, publishDateFrom, publishDateTo);

        return query.where(where).fetchCount();
    }

}
