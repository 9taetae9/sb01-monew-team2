package com.codeit.team2.monew.module.domain.article.external;


import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.external.url_provider.NewsUrlProvider;
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
public class ChosunRssNewsClientTest {

    private ChosunRssNewsClient client;
    private MockWebServer mockWebServer;
    private ArticleMapper articleMapper;

    @BeforeEach
    public void setup() {
        mockWebServer = new MockWebServer();
        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString())
            .build();

        articleMapper = new ArticleMapperImpl();
        NewsUrlProvider provider = Mockito.mock(NewsUrlProvider.class);

        BDDMockito.given(provider.getUrls())
            .willReturn(Set.of("/"));

        client = new ChosunRssNewsClient(webClient, articleMapper, provider);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchChosunArticles_success() {
        String xml = """
            <rss xmlns:atom="http://www.w3.org/2005/Atom" xmlns:content="http://purl.org/rss/1.0/modules/content/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:sy="http://purl.org/rss/1.0/modules/syndication/" xmlns:media="http://search.yahoo.com/mrss/" version="2.0">
                   <channel>
                       <title>
                        <![CDATA[ 조선일보 ]]>
                       </title>
                       <link>https://www.chosun.com</link>
                       <atom:link href="https://www.chosun.com/arc/outboundfeeds/rss/category/politics/" rel="self" type="application/rss+xml"/>
                       <description>
                        <![CDATA[ 1등 인터넷뉴스 조선닷컴 | 전체기사 ]]>
                       </description>
                       <lastBuildDate>Wed, 23 Apr 2025 00:17:11 +0000</lastBuildDate>
                       <language>ko</language>
                       <category>news</category>
                       <ttl>1</ttl>
                       <sy:updatePeriod>hourly</sy:updatePeriod>
                       <sy:updateFrequency>1</sy:updateFrequency>
                       <image>
                        <url>https://image.chosun.com/cs_logo.png</url>
                        <title>조선일보</title>
                        <link>https://www.chosun.com</link>
                       </image>
                       <item>
                           <title>
                            <![CDATA[ 尹 비판 여론이 국민의힘 1차 경선 갈랐다 ]]>
                           </title>
                           <link>https://www.chosun.com/politics/2025/04/23/ZNFXB3GDFRHRXJFFUHZPTF4YEY/</link>
                           <guid isPermaLink="true">https://www.chosun.com/politics/2025/04/23/ZNFXB3GDFRHRXJFFUHZPTF4YEY/</guid>
                           <dc:creator>
                            <![CDATA[ 김형원 기자, 양지혜 기자 ]]>
                           </dc:creator>
                           <description/>
                           <pubDate>Tue, 22 Apr 2025 15:59:48 +0000</pubDate>
                           <content:encoded>
                            <![CDATA[ <img src="https://www.chosun.com/resizer/v2/DZOH4LEHGNBSDAJG4SMKUWVZKE.jpg?width=1280&height=635&auth=2f95cfc69a31901faa17c4d1ea7edd2bfc59945dd49c74e0d675ae25f2275838&smart=true" alt="국민의힘이 22일 대선 2차 경선에 진출한 후보 4명을 발표했다. 왼쪽부터 김문수, 안철수, 한동훈, 홍준표 후보(가나다순). /연합뉴스" height="635" width="1280"/><p>국민의힘 대선 경선 후보를 8명에서 4명으로 압축하는 1차 예비 경선을 김문수 전 고용노동부 장관, 안철수 의원, 한동훈 전 국민의힘 대표, 홍준표(가나다순) 전 대구시장이 통과했다. 나경원 의원, 양향자 전 의원, 유정복 인천시장, 이철우 경북지사는 탈락했다. 국민의힘 선거관리위원회는 22일 이 같은 1차 예비 경선 결과를 발표했다. 1차 경선은 일반 국민을 대상으로 여론조사를 실시해 상위 4인을 추리는 방식으로 진행됐다. </p> ]]>
                           </content:encoded>
                           <media:content url="https://www.chosun.com/resizer/v2/DZOH4LEHGNBSDAJG4SMKUWVZKE.jpg?width=1280&height=635&auth=2f95cfc69a31901faa17c4d1ea7edd2bfc59945dd49c74e0d675ae25f2275838&smart=true" type="image/jpeg" height="635" width="1280">
                            <media:description type="plain">
                                <![CDATA[ 국민의힘이 22일 대선 2차 경선에 진출한 후보 4명을 발표했다. 왼쪽부터 김문수, 안철수, 한동훈, 홍준표 후보(가나다순). /연합뉴스 ]]>
                            </media:description>
                           </media:content>
                       </item>
                   </channel>
               </rss>
            """;

        mockWebServer.enqueue(new MockResponse()
            .setBody(xml)
            .addHeader("Content-Type", "application/xml"));

        List<Article> articles = client.fetchArticles();

        Assertions.assertThat(articles).hasSize(1);
        Assertions.assertThat(articles.get(0).getTitle()).contains("비판 여론이 국민의힘 1차 경선 갈랐다");
    }
}
