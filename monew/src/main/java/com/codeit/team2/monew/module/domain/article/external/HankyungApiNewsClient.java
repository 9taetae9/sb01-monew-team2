package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.rss.HankyungRss;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.url_provider.NewsUrlProvider;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor

// TODO : RSSNewsClient interface 생성
public class HankyungApiNewsClient {

    private final WebClient webClient;
    private final ArticleMapper articleMapper;

    @Qualifier("hankyung")
    private final NewsUrlProvider provider;


    public List<Article> fetchArticles() {
        XmlMapper xmlMapper = new XmlMapper();
        List<HankyungRss.Item> xmlResults = new ArrayList<>();

        try {
            for (String url : provider.getUrls()) {
                String xmlString = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

                HankyungRss rss = xmlMapper.readValue(xmlString, HankyungRss.class);

                xmlResults.addAll(rss.getItems());
            }
        } catch (JsonProcessingException e) {
            log.warn("Hankyung XML Fetch Failed: reason={}", e.getMessage());
            throw new RuntimeException(e);
        }

        for (HankyungRss.Item item : xmlResults) {
            if (item.getPubDate() == null) {
                System.out.println(item.getTitle() + " " + item.getAuthor());
            }
        }

        return articleMapper.hankyungRssListToEntity(xmlResults);
    }
}
