package com.codeit.team2.monew.module.domain.article.external;


import com.codeit.team2.monew.module.domain.article.dto.ArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.FetchArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
public class NaverNewsClientTest {

    private NaverNewsClient client;
    private MockWebServer mockServer;

    @BeforeEach
    void setup() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();

        WebClient webClient = WebClient.builder().baseUrl(mockServer.url("/").toString()).build();

        client = new NaverNewsClient(webClient, "dummy-id", "dummy-secret");
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

        FetchCommand cmd = new FetchCommand("AI", 10,1, "date");

        //when
        List<FetchArticleDto> result = client.fetchArticles(cmd);

        Assertions.assertThat(result.size()).isEqualTo(1);
    }
}
