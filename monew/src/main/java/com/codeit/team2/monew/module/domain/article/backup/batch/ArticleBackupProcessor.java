package com.codeit.team2.monew.module.domain.article.backup.batch;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.codeit.team2.monew.module.domain.article.backup.dto.ArticleBackupDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.interest.entity.Interest;
import com.codeit.team2.monew.module.domain.relation.entity.ArticleInterest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleBackupProcessor implements ItemProcessor<Article, ArticleBackupDto> {

    @Override
    public ArticleBackupDto process(Article article) throws Exception {
        // 관련 관심사 id 추출
        Set<UUID> interestIds = article.getArticleInterests().stream()
            .map(ArticleInterest::getInterest)
            .map(Interest::getId)
            .collect(Collectors.toSet());

        // 백업 dto 생성
        return new ArticleBackupDto(
            article.getId(),
            article.getTitle(),
            article.getSource(),
            article.getSourceUrl(),
            article.getSummary(),
            article.getViewCount(),
            article.getPublishedDate(),
            article.getDeleted(),
            interestIds,
            Instant.now()
        );
    }
}
