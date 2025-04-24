package com.codeit.team2.monew.module.domain.article.service;

import com.codeit.team2.monew.module.domain.article.dto.ArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.dto.CursorPageResponseArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.request.ArticleOrderBy;
import com.codeit.team2.monew.module.domain.article.dto.request.CursorPageRequestArticleDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import com.codeit.team2.monew.module.domain.article.repository.ArticleCustomRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleViewRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleViewRepository articleViewRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;
    private final ArticleCustomRepository articleCustomRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ArticleViewDto createUserArticleView(UUID userId, UUID articleId) {

        Article article = getArticleOrThrow(articleId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.debug("User Not Found: id={}", userId);
            return new IllegalArgumentException();
        });

        ArticleView articleView = articleViewRepository.findByUserAndArticle(user, article)
            .orElseGet(() -> {
                article.incrementView();
                return articleViewRepository.save(new ArticleView(user, article, Instant.now()));
            });

        // TODO : CommentRepository 완성시 관련 Comment 조회 로직
        return articleMapper.toResponseDto(article, articleView, userId, 0);
    }

    @Override
    public void softDelete(UUID articleId) {
        Article article = getArticleOrThrow(articleId);

        article.softDelete();
        // TODO : soft delete 시 연관 entity 어떻게 처리할 지
    }

    @Override
    public void hardDelete(UUID articleId) {
        Article article = getArticleOrThrow(articleId);

        articleRepository.delete(article);

        // TODO : Comment 도메인 완성시 같이 삭제 (CascadeType)
    }

    private Article getArticleOrThrow(UUID articleId) {
        Article optionalArticle = articleRepository.findById(articleId)
            .orElseThrow(() -> {
                log.debug("Article Not Found: id={}", articleId);
                return new IllegalArgumentException();
            });
        return optionalArticle;
    }

    @Override
    public CursorPageResponseArticleDto findAll(UUID userId,
        CursorPageRequestArticleDto cursorPageRequestArticleDto) {
        Slice<Article> slices;
        Pageable pageable = PageRequest.of(0, cursorPageRequestArticleDto.limit(), Sort.unsorted());
        if (cursorPageRequestArticleDto.orderBy().equals(ArticleOrderBy.publishDate)) {
            slices = articleCustomRepository.findByPublishDate(
                cursorPageRequestArticleDto.keyword(),
                cursorPageRequestArticleDto.interestId(),
                cursorPageRequestArticleDto.sourceIn(),
                cursorPageRequestArticleDto.getPublishDateFromInstant(),
                cursorPageRequestArticleDto.getPublishDateToInstant(),
                cursorPageRequestArticleDto.direction(),
                cursorPageRequestArticleDto.cursor(),
                cursorPageRequestArticleDto.after(),
                pageable);
        } else if (cursorPageRequestArticleDto.orderBy().equals(ArticleOrderBy.viewCount)) {
            slices = articleCustomRepository.findByViewCount(cursorPageRequestArticleDto.keyword(),
                cursorPageRequestArticleDto.interestId(),
                cursorPageRequestArticleDto.sourceIn(),
                cursorPageRequestArticleDto.getPublishDateFromInstant(),
                cursorPageRequestArticleDto.getPublishDateToInstant(),
                cursorPageRequestArticleDto.direction(),
                cursorPageRequestArticleDto.cursor(),
                cursorPageRequestArticleDto.after(),
                pageable);
        } else if (cursorPageRequestArticleDto.orderBy().equals(ArticleOrderBy.commentCount)) {
            slices = articleCustomRepository.findByCommentCount(
                cursorPageRequestArticleDto.keyword(),
                cursorPageRequestArticleDto.interestId(),
                cursorPageRequestArticleDto.sourceIn(),
                cursorPageRequestArticleDto.getPublishDateFromInstant(),
                cursorPageRequestArticleDto.getPublishDateToInstant(),
                cursorPageRequestArticleDto.direction(),
                cursorPageRequestArticleDto.cursor(),
                cursorPageRequestArticleDto.after(),
                pageable);
        } else {
            slices = null;
        }

        List<Article> articles = slices.getContent();
        List<ArticleDto> articleDtos = new ArrayList<>();
        articles.stream().forEach(article -> {
            Long commentCount = commentRepository.countByArticle(article);
            boolean viewedByMe = articleViewRepository.existsByUserIdAndArticleId(userId,
                article.getId());
            articleDtos.add(new ArticleDto(article.getId(),
                article.getSource(),
                article.getSourceUrl(),
                article.getTitle(),
                article.getPublishedDate(),
                article.getSummary(),
                commentCount,
                article.getViewCount(),
                viewedByMe));
        });

        long totalElements = articleCustomRepository.countFilteredTotalElements(
            cursorPageRequestArticleDto.keyword(), cursorPageRequestArticleDto.interestId(),
            cursorPageRequestArticleDto.sourceIn(),
            cursorPageRequestArticleDto.getPublishDateFromInstant(),
            cursorPageRequestArticleDto.getPublishDateToInstant());

        boolean hasNext = slices.hasNext();

        Object cursor = null;
        if (hasNext && !articles.isEmpty()) {
            ArticleDto last = articleDtos.get(articleDtos.size() - 1);
            switch (cursorPageRequestArticleDto.orderBy()) {
                case publishDate -> cursor = last.publishDate();
                case viewCount -> cursor = last.viewCount();
                case commentCount -> cursor = last.commentCount();
            }
        }

        Instant after =
            (hasNext && !articles.isEmpty()) ? articles.get(articles.size() - 1).getCreatedAt()
                : null;

        return new CursorPageResponseArticleDto(articleDtos, cursor, after, articles.size(),
            totalElements, hasNext);
    }
}
