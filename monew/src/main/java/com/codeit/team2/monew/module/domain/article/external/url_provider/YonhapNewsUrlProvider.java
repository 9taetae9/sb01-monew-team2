package com.codeit.team2.monew.module.domain.article.external.url_provider;

import java.util.Set;

public class YonhapNewsUrlProvider implements NewsUrlProvider {

    private final static Set<String> yonhapUrlSet = Set.of(
        "http://www.yonhapnewstv.co.kr/browse/feed/",
        "http://www.yonhapnewstv.co.kr/category/news/headline/feed/",
        "http://www.yonhapnewstv.co.kr/category/news/politics/feed/",
        "http://www.yonhapnewstv.co.kr/category/news/economy/feed/",
        "http://www.yonhapnewstv.co.kr/category/news/society/feed/",
        "http://www.yonhapnewstv.co.kr/category/news/local/feed/",
        "http://www.yonhapnewstv.co.kr/category/news/international/feed/",
        "http://www.yonhapnewstv.co.kr/category/news/culture/feed/",
        "http://www.yonhapnewstv.co.kr/category/news/sports/feed/",
        "http://www.yonhapnewstv.co.kr/category/news/weather/feed/"
    );

    @Override
    public Set<String> getUrls() {
        return yonhapUrlSet;
    }
}
