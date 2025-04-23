package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.rss.ChosunRss;
import com.codeit.team2.monew.module.domain.article.dto.rss.ChosunRss.Item;
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
@Qualifier("chosunRssNewsClient")
public class ChosunRssNewsClient extends AbstractRssNewsClient<ChosunRss, ChosunRss.Item> {

    private final ArticleMapper articleMapper;

    public ChosunRssNewsClient(@Qualifier("redirectClient") WebClient webClient,
        ArticleMapper articleMapper,
        @Qualifier("chosun") NewsUrlProvider provider) {
        super(webClient, provider);
        this.articleMapper = articleMapper;
    }


    @Override
    protected List<DummyArticle> mapToArticles(List<Item> allItems) {
        return articleMapper.chosunRssListToEntity(allItems);
    }

    @Override
    ChosunRss parseXmlToDto(String xml) throws JsonProcessingException {
        return new XmlMapper().readValue(xml, ChosunRss.class);
    }

    @Override
    List<Item> extractItems(ChosunRss rss) {
        return rss.getItems();
    }
}
