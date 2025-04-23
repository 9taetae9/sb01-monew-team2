package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.rss.ChosunRss;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.url_provider.NewsUrlProvider;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Component
@Qualifier("chosunRssNewsClient")
public class ChosunRssNewsClient implements RssNewsClient {

    private final WebClient webClient;
    private final ArticleMapper articleMapper;

    private final NewsUrlProvider provider;

    public ChosunRssNewsClient(WebClient webClient, ArticleMapper articleMapper,
        @Qualifier("chosun") NewsUrlProvider provider) {
        this.webClient = webClient;
        this.articleMapper = articleMapper;
        this.provider = provider;
    }

    @Override
    public List<Article> fetchArticles() {
        XmlMapper xmlMapper = new XmlMapper();
        List<ChosunRss.Item> xmlResults = new ArrayList<>();

        try {
            for (String url : provider.getUrls()) {
                String xmlString = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

                ChosunRss rss = xmlMapper.readValue(xmlString, ChosunRss.class);

                xmlResults.addAll(rss.getItems());
            }
        } catch (JsonProcessingException e) {
            log.warn("Chosun XML Fetch Failed: reason={}", e.getMessage());
            throw new RuntimeException(e);
        }

        return articleMapper.chosunRssListToEntity(xmlResults);
    }
}
