package com.codeit.team2.monew.module.domain.article.external.url_provider;

import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("chosun")
public class ChosunNewsUrlProvider implements NewsUrlProvider {

    private static final Set<String> chosunUrlSet = Set.of(
        "https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml",
        "https://www.chosun.com/arc/outboundfeeds/rss/category/politics/?outputType=xml",
        "https://www.chosun.com/arc/outboundfeeds/rss/category/economy/?outputType=xml",
        "https://www.chosun.com/arc/outboundfeeds/rss/category/national/?outputType=xml",
        "https://www.chosun.com/arc/outboundfeeds/rss/category/international/?outputType=xml",
        "https://www.chosun.com/arc/outboundfeeds/rss/category/culture-life/?outputType=xml",
        "https://www.chosun.com/arc/outboundfeeds/rss/category/opinion/?outputType=xml",
        "https://www.chosun.com/arc/outboundfeeds/rss/category/sports/?outputType=xml",
        "https://www.chosun.com/arc/outboundfeeds/rss/category/entertainments/?outputType=xml"
    );


    @Override
    public Set<String> getUrls() {
        return chosunUrlSet;
    }
}
