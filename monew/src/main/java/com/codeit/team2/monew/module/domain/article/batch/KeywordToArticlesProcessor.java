package com.codeit.team2.monew.module.domain.article.batch;

import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.NaverNewsClient;
import com.codeit.team2.monew.module.domain.interest.entity.Keyword;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeywordToArticlesProcessor implements ItemProcessor<Keyword, List<Article>> {

    private final NaverNewsClient naverNewsClient;

    @Override
    public List<Article> process(Keyword keyword) throws Exception {
        List<Article> allArticles = new ArrayList<>();
        int start = 1;
        int display = 100;

        while (start <= 1000) {
            FetchCommand cmd = new FetchCommand(keyword.getName(), display, start, "date");
            List<Article> articles = naverNewsClient.fetchArticles(cmd);

            if (articles.isEmpty() || articles.size() < display) {
                allArticles.addAll(articles);
                break;
            }

            allArticles.addAll(articles);
            start += display;
        }

        return allArticles;
    }
}
