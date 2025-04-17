package com.codeit.team2.monew.module.domain.article.external;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootTest
@Disabled
public class LiveNaverNewsClientTest {

    @Autowired
    @Qualifier("naverNewsClient")
    private WebClient webClient;

    @Autowired
    private ArticleMapper articleMapper;

    @Value("${news.naver.client-id}")
    private String clientId;

    @Value("${news.naver.client-secret}")
    private String clientSecret;

    @BeforeAll
    static void setup() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(".env"));
        System.setProperty("NAVER_CLIENT_ID", properties.getProperty("NAVER_CLIENT_ID"));
        System.setProperty("NAVER_CLIENT_SECRET", properties.getProperty("NAVER_CLIENT_SECRET"));
    }

    @Test
    @DisplayName("네이버 API 실 호출 테스트")
    void realFetch() throws Exception {

        // given
        NaverNewsClient client = new NaverNewsClient(webClient, clientId, clientSecret,
            articleMapper);
        FetchCommand cmd = new FetchCommand("AI", 10, 1, "date");

        // when
        List<Article> result = client.fetchArticles(cmd);

        // then
        assertThat(result).isNotEmpty();
        result.forEach(article -> {
            System.out.println("제목: " + article.getTitle());
            System.out.println("링크: " + article.getSourceUrl());
            System.out.println("발행일: " + article.getPublishedDate());
            System.out.println("----");
        });
    }
}
