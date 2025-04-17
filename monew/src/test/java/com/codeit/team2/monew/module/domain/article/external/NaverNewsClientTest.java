package com.codeit.team2.monew.module.domain.article.external;


import java.io.IOException;
import java.time.Instant;
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
                                "originallink": "http://testlink.com",
                                "link": "https://testlink.com",
                                "description" : "test description",
                                "pubDate" : "Fri, 11 May 2028 09:50:00 +0900"
                            }
                        ]
                    }
                    """
            ).addHeader("Content-Type", "application/json"));

        //when
        List<NaverArticle> result = client.fetchArticles("AI", 1, Instant.now());

        Assertions.assertThat(result.size()).isEqualTo(1);
    }
}
