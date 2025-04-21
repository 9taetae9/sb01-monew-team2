package com.codeit.team2.monew.module.domain.article.service;

import com.codeit.team2.monew.module.domain.article.dto.ArticleViewDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.ArticleView;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.article.repository.ArticleViewRepository;
import com.codeit.team2.monew.module.domain.user.entity.User;
import com.codeit.team2.monew.module.domain.user.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleViewRepository articleViewRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;

    @Override
    public ArticleViewDto createUserArticleView(UUID userId, UUID articleId) {

        Article article = articleRepository.findById(articleId).orElseThrow(() -> {
            log.debug("Article Not Found: id={}", articleId);
            return new IllegalArgumentException();
        });

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
        Article optionalArticle = articleRepository.findById(articleId)
            .orElseThrow(() -> {
                log.debug("Article Not Found: id={}", articleId);
                return new IllegalArgumentException();
            });

        optionalArticle.softDelete();
        // TODO : soft delete 시 연관 entity 어떻게 처리할 지
    }
}
