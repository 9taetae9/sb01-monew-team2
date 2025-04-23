package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.url_provider.NewsUrlProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class AbstractRssNewsClient<T, R> implements RssNewsClient {

    private final WebClient webClient;
    private final NewsUrlProvider provider;

    public AbstractRssNewsClient(WebClient webClient, NewsUrlProvider provider) {
        this.webClient = webClient;
        this.provider = provider;
    }

    @Override
    public List<Article> fetchArticles() {

        List<R> allItems = new ArrayList<>();

        try {
            for (String url : provider.getUrls()) {
                String xmlString = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

                T rss = parseXmlToDto(xmlString);
                allItems.addAll(extractItems(rss));
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }

        return mapToArticles(allItems);
    }

    protected abstract List<Article> mapToArticles(List<R> allItems);

    abstract T parseXmlToDto(String xml) throws JsonProcessingException;

    abstract List<R> extractItems(T rss);
}
