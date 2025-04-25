package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.rss.YonhapRss;
import com.codeit.team2.monew.module.domain.article.dto.rss.YonhapRss.Item;
import com.codeit.team2.monew.module.domain.article.entity.DummyArticle;
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
@Qualifier("yonhapRssNewsClient")
public class YonhapRssNewsClient extends AbstractRssNewsClient<YonhapRss, YonhapRss.Item> {

    private final ArticleMapper articleMapper;

    public YonhapRssNewsClient(@Qualifier("redirectClient") WebClient webClient,
        ArticleMapper articleMapper,
        @Qualifier("yonhap") NewsUrlProvider newsUrlProvider) {
        super(webClient, newsUrlProvider);
        this.articleMapper = articleMapper;
    }


    @Override
    protected List<DummyArticle> mapToArticles(List<Item> allItems) {
        return articleMapper.yonhapRssListToEntity(allItems);
    }

    @Override
    YonhapRss parseXmlToDto(String xml) throws JsonProcessingException {
        return new XmlMapper().readValue(xml, YonhapRss.class);
    }

    @Override
    List<Item> extractItems(YonhapRss rss) {
        return rss.getItems();
    }
}
