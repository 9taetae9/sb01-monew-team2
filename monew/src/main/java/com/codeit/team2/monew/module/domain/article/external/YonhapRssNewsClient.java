package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.rss.YonhapRss;
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
@Qualifier("yonhapRssNewsClient")
public class YonhapRssNewsClient implements RssNewsClient {

    private final WebClient webClient;
    private final ArticleMapper articleMapper;
    private final NewsUrlProvider provider;

    public YonhapRssNewsClient(@Qualifier("redirectClient") WebClient webClient,
        ArticleMapper articleMapper,
        @Qualifier("yonhap") NewsUrlProvider newsUrlProvider) {
        this.webClient = webClient;
        this.articleMapper = articleMapper;
        this.provider = newsUrlProvider;
    }

    @Override
    public List<Article> fetchArticles() {

        XmlMapper xmlMapper = new XmlMapper();
        List<YonhapRss.Item> xmlResults = new ArrayList<>();

        try {
            for (String url : provider.getUrls()) {
                String xmlString = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

                YonhapRss rss = xmlMapper.readValue(xmlString, YonhapRss.class);

                xmlResults.addAll(rss.getItems());
            }
        } catch (JsonProcessingException e) {
            log.warn("Yonhap XML Fetch Failed: reason={}", e.getMessage());
            throw new RuntimeException(e);
        }

        return articleMapper.yonhapRssListToEntity(xmlResults);
    }
}
