package com.codeit.team2.monew.module.domain.article.batch;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.repository.ArticleRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchArticleWriter implements ItemWriter<List<Article>> {

    private final ArticleRepository articleRepository;

    @Override
    public void write(Chunk<? extends List<Article>> items) throws Exception {
        List<Article> flatList = new ArrayList<>();

        for (List<Article> list : items) {
            flatList.addAll(list);
        }

        articleRepository.saveAll(flatList);
    }
}
