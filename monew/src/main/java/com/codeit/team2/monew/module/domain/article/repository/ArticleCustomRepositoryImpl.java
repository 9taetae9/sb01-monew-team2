package com.codeit.team2.monew.module.domain.article.repository;

import com.codeit.team2.monew.module.domain.article.dto.request.ArticleSourceIn;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.QArticle;
import com.codeit.team2.monew.module.domain.comment.entity.QComment;
import com.codeit.team2.monew.module.domain.relation.entity.QArticleInterest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleCustomRepositoryImpl implements ArticleCustomRepository {

    private final JPAQueryFactory queryFactory;

    private BooleanBuilder buildCommonFilters(QArticle article, QArticleInterest articleInterest,
        String keyword, UUID interestId, List<ArticleSourceIn> sourceIn,
        Instant publishDateFrom, Instant publishDateTo, JPAQuery<Article> query) {

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
            query.leftJoin(article.articleInterests, articleInterest);
            where.and(articleInterest.interest.id.eq(interestId));
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

    @Override
    public long countFilteredTotalElements(String keyword, UUID interestId,
        List<ArticleSourceIn> sourceIn, Instant publishDateFrom, Instant publishDateTo) {
        QArticle article = QArticle.article;
        QArticleInterest articleInterest = QArticleInterest.articleInterest;

        JPAQuery<Article> query = queryFactory.selectFrom(article);
        BooleanBuilder where = buildCommonFilters(article, articleInterest,
            keyword, interestId, sourceIn, publishDateFrom, publishDateTo, query);

        return query.where(where).fetchCount();
    }

    @Override
    public Slice<Article> findByPublishDate(String keyword, UUID interestId,
        List<ArticleSourceIn> sourceIn, Instant publishDateFrom, Instant publishDateTo,
        Direction direction, String cursor, Instant after, Pageable pageable) {

        QArticle article = QArticle.article;
        QArticleInterest articleInterest = QArticleInterest.articleInterest;

        JPAQuery<Article> query = queryFactory.selectFrom(article).distinct();

        BooleanBuilder where = buildCommonFilters(article, articleInterest,
            keyword, interestId, sourceIn, publishDateFrom, publishDateTo, query);

        // 커서 조건 확인: 커서 없으면 생략
        if (cursor != null && after != null) {
            // Object -> Instant
            Instant publishDateCursor = Instant.parse(cursor);
            // ASC
            if (direction.isAscending()) {
                where.and(article.publishedDate.gt(publishDateCursor))
                    .or(article.publishedDate.eq(publishDateCursor)
                        .and(article.createdAt.gt(after)));
            } else {
                //DESC
                where.and(article.publishedDate.lt(publishDateCursor))
                    .or(article.publishedDate.eq(publishDateCursor)
                        .and(article.createdAt.lt(after)));
            }
        }

        query.where(where);

        // 정렬 조건: publishedDate + createdAt
        Order order = direction.isAscending() ? Order.ASC : Order.DESC;
        query.orderBy(
            new OrderSpecifier<>(order, article.publishedDate),
            new OrderSpecifier<>(order, article.createdAt));

        List<Article> result = query.limit(pageable.getPageSize() + 1).fetch();

        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) {
            result.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }

    @Override
    public Slice<Article> findByViewCount(String keyword, UUID interestId,
        List<ArticleSourceIn> sourceIn, Instant publishDateFrom, Instant publishDateTo,
        Direction direction, String cursor, Instant after, Pageable pageable) {
        QArticle article = QArticle.article;
        QArticleInterest articleInterest = QArticleInterest.articleInterest;

        JPAQuery<Article> query = queryFactory.selectFrom(article).distinct();

        BooleanBuilder where = buildCommonFilters(article, articleInterest,
            keyword, interestId, sourceIn, publishDateFrom, publishDateTo, query);

        // 커서 조건 확인: 커서 없으면 생략
        if (cursor != null && after != null) {
            // Object -> Long
            Long viewCountCursor = Long.parseLong(cursor);
            // ASC
            if (direction.isAscending()) {
                where.and(article.viewCount.gt(viewCountCursor))
                    .or(article.viewCount.eq(viewCountCursor)
                        .and(article.createdAt.gt(after)));
            } else {
                //DESC
                where.and(article.viewCount.lt(viewCountCursor))
                    .or(article.viewCount.eq(viewCountCursor)
                        .and(article.createdAt.lt(after)));
            }
        }

        query.where(where);

        // 정렬 조건: publishedDate + createdAt
        Order order = direction.isAscending() ? Order.ASC : Order.DESC;
        query.orderBy(
            new OrderSpecifier<>(order, article.viewCount),
            new OrderSpecifier<>(order, article.createdAt));

        List<Article> result = query.limit(pageable.getPageSize() + 1).fetch();

        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) {
            result.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(result, pageable, hasNext);
    }

    @Override
    public Slice<Article> findByCommentCount(String keyword, UUID interestId,
        List<ArticleSourceIn> sourceIn, Instant publishDateFrom, Instant publishDateTo,
        Direction direction, String cursor, Instant after, Pageable pageable) {
        QArticle article = QArticle.article;
        QArticleInterest articleInterest = QArticleInterest.articleInterest;
        QComment comment = QComment.comment;

        JPAQuery<Article> query = queryFactory
            .select(article)
            .from(article)
            .leftJoin(article.articleInterests, articleInterest)
            .leftJoin(comment).on(comment.article.eq(article))
            .groupBy(article.id);

        BooleanBuilder where = buildCommonFilters(article, articleInterest,
            keyword, interestId, sourceIn, publishDateFrom, publishDateTo, query);

        if (cursor != null && after != null) {
            Long commentCursor = Long.parseLong(cursor);
            if (direction.isAscending()) {
                query.having(comment.count().gt(commentCursor)
                    .or(comment.count().eq(commentCursor).and(article.createdAt.gt(after))));
            } else {
                query.having(comment.count().lt(commentCursor)
                    .or(comment.count().eq(commentCursor).and(article.createdAt.lt(after))));
            }
        }

        query.where(where);

        Order order = direction.isAscending() ? Order.ASC : Order.DESC;
        query.orderBy(
            new OrderSpecifier<>(order, comment.count()),
            // commentCount 기준으로 세야함 -> 쿼리에서 left join으로 가져온 comment의 수
            new OrderSpecifier<>(order, article.createdAt)
        );

        List<Article> articles = query
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = articles.size() > pageable.getPageSize();
        if (hasNext) {
            articles.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(articles, pageable, hasNext);
    }
}
