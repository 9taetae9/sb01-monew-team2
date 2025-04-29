package com.codeit.team2.monew.module.domain.article.service;

import com.codeit.team2.monew.module.domain.article.dto.ArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.dto.CursorPageResponseArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.request.CursorPageRequestArticleDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.article.event.ArticleViewCreateEvent;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleViewRepository;
import com.codeit.team2.monew.module.domain.comment.repository.CommentRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
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
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher publisher;

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

        // article view 생성 이벤트 발생
        publisher.publishEvent(new ArticleViewCreateEvent(
            articleView,
            user
        ));

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
        Slice<Article> slices = articleRepository.findWithCursor(cursorPageRequestArticleDto);

        List<Article> articles = slices.getContent();
        List<UUID> articleIds = articles.stream().map(article -> article.getId())
            .collect(Collectors.toList());
        // DTO 매핑을 위해 필요한 값 bulk로 가져오기
        Map<UUID, Long> commentCountMap = commentRepository.countByArticleIds(articleIds).stream()
            .collect(Collectors.toMap(
                row -> (UUID) row[0],  // 첫 번째 컬럼: articleId
                row -> (Long) row[1]   // 두 번째 컬럼: count
            ));
        List<UUID> viewedArticleIds = articleViewRepository.findViewedArticleIds(userId,
            articleIds);

        // Article -> ArticleDto 매핑
        List<ArticleDto> articleDtos = articles.stream()
            .map(article -> {
                Long commentCount = commentCountMap.getOrDefault(article.getId(), 0L);
                boolean viewedByMe = viewedArticleIds.contains(article.getId());
                return articleMapper.toDto(article, commentCount, viewedByMe);
            })
            .toList();

        long totalElements = articleRepository.countFilteredTotalElements(
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
