package com.codeit.team2.monew.module.domain.article.external.url_provider;


import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("hankyung")
public class HankyungNewsUrlProvider implements NewsUrlProvider {

    private static final Set<String> hankyungUrlSet = Set.of(
        "https://www.hankyung.com/feed/all-news",
        "https://www.hankyung.com/feed/economy",
        "https://www.hankyung.com/feed/it",
        "https://www.hankyung.com/feed/international",
        "https://www.hankyung.com/feed/life",
        "https://www.hankyung.com/feed/sports",
//        "https://www.hankyung.com/feed/video", 추후 파싱 예정 (응답 포멧이 다름)
        "https://www.hankyung.com/feed/finance",
        "https://www.hankyung.com/feed/realestate",
        "https://www.hankyung.com/feed/politics",
        "https://www.hankyung.com/feed/society",
        "https://www.hankyung.com/feed/opinion",
        "https://www.hankyung.com/feed/entertainment"
    );

    @Override
    public Set<String> getUrls() {
        return hankyungUrlSet;
    }
}
