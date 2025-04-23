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
public class YonhapRssNewsClientTest {


    private YonhapRssNewsClient client;
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

        client = new YonhapRssNewsClient(webClient, articleMapper, provider);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchYonhapArticles_success() {
        String xml = """
            <rss xmlns:content="http://purl.org/rss/1.0/modules/content/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:sy="http://purl.org/rss/1.0/modules/syndication/" xmlns:slash="http://purl.org/rss/1.0/modules/slash/" version="2.0">
                <channel>
                    <title>연합뉴스TV :: 대한민국 뉴스의 시작. 채널 23 » 최신</title>
                    <atom:link href="https://www.yonhapnewstv.co.kr/browse/feed/" rel="self" type="application/rss+xml"/>
                    <link>https://www.yonhapnewstv.co.kr</link>
                    <description/>
                    <lastBuildDate>Wed, 23 Apr 2025 10:24:33 +0900</lastBuildDate>
                    <language>ko-KR</language>
                    <sy:updatePeriod>hourly</sy:updatePeriod>
                    <sy:updateFrequency>1</sy:updateFrequency>
                    <generator>https://www.yonhapnewstv.co.kr</generator>
                    <item>
                        <title>
                            <![CDATA[ 증선위, '삼부토건 주가조작 의혹' 사건 고발안건 의결 예정 ]]>
                        </title>
                        <link>https://www.yonhapnewstv.co.kr/news/MYH20250423102414902</link>
                        <comments>https://www.yonhapnewstv.co.kr/news/MYH20250423102414902#comments</comments>
                        <pubDate>Wed, 23 Apr 2025 10:24:33 +0900</pubDate>
                        <dc:creator>장한별</dc:creator>
                        <category>
                            <![CDATA[ 최신 ]]>
                        </category>
                        <guid isPermaLink="false">MYH20250423102414902</guid>
                        <enclosure url="https://d2k5miyk6y5zf0.cloudfront.net/article/MYH/20250423/MYH20250423102414902_P1.jpg" type="image/jpeg"/>
                        <description>
                            <![CDATA[ 금융위원회 산하 증권선물위원회가 오늘(23일) 정례회의에서 삼부토건 주가조작 의혹의 핵심 관계자들을 검찰 고발하는 안을 의결할 예정입니다. 삼부토건은 지난 2023년 폴란드에서 열린 우크라이나 글로벌 재건 포럼에 참석한 뒤 우크라이나 재건주로 분류되면서 1천원대였던 주가가 같은 해 7월 장중 5,500원까지 급등한 기업입니다. 이와 관련해 이복현 금융감독원장은 지난 2일 "(조사가) 4월 중 마무리가 가능하다"면서 '김건희 여사와 관련된 ... ]]>
                        </description>
                        <content:encoded>
                            <![CDATA[ 금융위원회 산하 증권선물위원회가 오늘(23일) 정례회의에서 삼부토건 주가조작 의혹의 핵심 관계자들을 검찰 고발하는 안을 의결할 예정입니다. 삼부토건은 지난 2023년 폴란드에서 열린 우크라이나 글로벌 재건 포럼에 참석한 뒤 우크라이나 재건주로 분류되면서 1천원대였던 주가가 같은 해 7월 장중 5,500원까지 급등한 기업입니다. 이와 관련해 이복현 금융감독원장은 지난 2일 "(조사가) 4월 중 마무리가 가능하다"면서 '김건희 여사와 관련된 부분도 나온 게 있느냐'는 질문에 "절차에 따라 볼 수 있는 것들은 다 보려고 한다"고 답한 바 있습니다. 금감원은 삼부토건 이해관계자가 낸 100억원대 차익이 도이치모터스 주가조작 의혹의 주요 인물로 알려진 이종호 전 블랙펄인베스트먼트 대표 측에 흘러 들어갔는지 자금을 추적해 왔습니다. 장한별 기자 #윤석열 #김건희 #주가조작 #삼부토건 #금감원 #원희룡 연합뉴스TV 기사문의 및 제보 : 카톡/라인 jebo23 장한별(good_star@yna.co.kr) ]]>
                        </content:encoded>
                        <slash:comments>0</slash:comments>
                    </item>
                </channel>
            </rss>
            """;

        mockWebServer.enqueue(new MockResponse()
            .setBody(xml)
            .addHeader("Content-Type", "application/xml"));

        List<Article> articles = client.fetchArticles();

        Assertions.assertThat(articles).hasSize(1);
        Assertions.assertThat(articles.get(0).getTitle()).contains("사건 고발안건 의결 예정");
    }
}
