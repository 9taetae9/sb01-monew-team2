package com.codeit.team2.monew.module.domain.article.service;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import com.codeit.team2.monew.module.domain.article.repository.DummyArticleRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DummyArticleService {

    private final DummyArticleRepository dummyArticleRepository;
    private final ArticleRepository articleRepository;


    public void removeDuplicateArticles() {
        List<DummyArticle> dummyArticles = dummyArticleRepository.findAll();
        Set<String> urls = dummyArticles.stream().map(DummyArticle::getSourceUrl)
            .collect(Collectors.toSet());
        List<String> existingArticles = articleRepository.findAllBySourceUrlIn(urls).stream()
            .map(Article::getSourceUrl).toList();

        dummyArticleRepository.deleteAllBySourceUrlIn(existingArticles);
    }

}
