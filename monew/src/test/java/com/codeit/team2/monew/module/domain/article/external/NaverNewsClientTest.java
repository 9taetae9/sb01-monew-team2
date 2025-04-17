package com.codeit.team2.monew.module.domain.article.external;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
public class NaverNewsClientTest {

    private NaverNewsClient client;
    private MockWebServer mockServer;
    private ArticleMapper mapper;

    @BeforeEach
    void setup() throws IOException {
        mapper = mock(ArticleMapper.class);
        mockServer = new MockWebServer();
        mockServer.start();

        WebClient webClient = WebClient.builder().baseUrl(mockServer.url("/").toString()).build();

        client = new NaverNewsClient(webClient, "dummy-id", "dummy-secret", mapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void fetchNews_success() {
        //given
        mockServer.enqueue(new MockResponse()
            .setBody(
                """
                    {
                        "items": [
                            {
                                "title": "AI",
                                "originallink": "https://testlink.com",
                                "link": "https://testlink.com",
                                "description" : "test description",
                                "pubDate" : "Fri, 11 May 2028 09:50:00 +0900"
                            }
                        ]
                    }
                    """
            ).addHeader("Content-Type", "application/json"));

        FetchCommand cmd = new FetchCommand("AI", 10, 1, "date");
        Article article = new Article("AI", "NAVER", "https://testlink.com", "test description", 0,
            Instant.now(), false);
        BDDMockito.given(mapper.naverArticleListToEntity(any())).willReturn(List.of(article));

        //when
        List<Article> result = client.fetchArticles(cmd);

        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getSource()).isEqualTo("NAVER");
    }
}
