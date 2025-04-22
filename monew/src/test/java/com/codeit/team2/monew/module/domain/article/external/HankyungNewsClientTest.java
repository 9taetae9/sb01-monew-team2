package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapperImpl;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
public class HankyungNewsClientTest {

    private HankyungNewsClient client;
    private MockWebServer mockWebServer;
    private ArticleMapper articleMapper;

    @BeforeEach
    public void setUp() {
        mockWebServer = new MockWebServer();
        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString())
            .build();
        articleMapper = new ArticleMapperImpl();

        NewsUrlProvider provider = Mockito.mock(NewsUrlProvider.class);

        BDDMockito.given(provider.getUrls())
            .willReturn(Set.of("/test"));

        client = new HankyungNewsClient(webClient, articleMapper, provider);
    }

    @AfterEach
    void testDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchHankyungArticles_success() {
        String dummyXml = """
            <rss version="2.0">
                <channel>
                    <title>
                        <![CDATA[ 한국경제 | 전체뉴스 ]]>
                    </title>
                    <link>https://www.hankyung.com/all-news</link>
                    <language>ko</language>
                    <copyright>Copyright (c) 2005 hankyung.com All rights reserved</copyright>
                    <lastBuildDate>Tue, 22 Apr 2025 14:23:53 +0900</lastBuildDate>
                    <description>한경닷컴 RSS 서비스</description>
                    <item>
                        <title>
                            <![CDATA[ "콜드플레이 콘서트서 남친 목말 탔다가 욕 먹었어요" ]]>
                        </title>
                        <link>
                            <![CDATA[ https://www.hankyung.com/article/202504227694H ]]>
                        </link>
                        <author>
                            <![CDATA[ 김예랑 ]]>
                        </author>
                        <pubDate>Tue, 22 Apr 2025 14:15:03 +0900</pubDate>
                    </item>
                </channel>
            </rss>
            """;

        mockWebServer.enqueue(new MockResponse()
            .setBody(dummyXml)
            .addHeader("Content-Type", "application/xml"));

        List<Article> articles = client.fetchArticles(new FetchCommand("test", 0, 0, "test"));

        Assertions.assertThat(articles).hasSize(1);
    }
}
