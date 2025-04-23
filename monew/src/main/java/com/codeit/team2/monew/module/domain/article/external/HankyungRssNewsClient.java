package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.rss.HankyungRss;
import com.codeit.team2.monew.module.domain.article.dto.rss.HankyungRss.Item;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.url_provider.NewsUrlProvider;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@Qualifier("hankyungRssNewsClient")
public class HankyungRssNewsClient extends AbstractRssNewsClient<HankyungRss, HankyungRss.Item> {

    private final ArticleMapper articleMapper;

    public HankyungRssNewsClient(@Qualifier("redirectClient") WebClient webClient,
        ArticleMapper articleMapper,
        @Qualifier("hankyung") NewsUrlProvider provider) {
        super(webClient, provider);
        this.articleMapper = articleMapper;
    }

    @Override
    protected List<Article> mapToArticles(List<Item> allItems) {
        return articleMapper.hankyungRssListToEntity(allItems);
    }

    @Override
    HankyungRss parseXmlToDto(String xml) throws JsonProcessingException {
        return new XmlMapper().readValue(xml, HankyungRss.class);
    }

    @Override
    List<Item> extractItems(HankyungRss rss) {
        return rss.getItems();
    }
}
