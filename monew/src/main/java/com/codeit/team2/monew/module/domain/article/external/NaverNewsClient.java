package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.dto.NaverArticleResponseDto;
import com.codeit.team2.monew.module.domain.article.entity.Article;
import com.codeit.team2.monew.module.domain.article.mapper.ArticleMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NaverNewsClient implements NewsClient {

    private WebClient webClient;
    private String clientId;
    private String clientSecret;
    private final ArticleMapper articleMapper;


    public NaverNewsClient(
        @Qualifier("naverNewsClient") WebClient webClient,
        @Value(value = "${news.naver.client-id}") String clientId,
        @Value(value = "${news.naver.client-secret}") String clientSecret,
        ArticleMapper articleMapper) {
        this.webClient = webClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.articleMapper = articleMapper;
    }

    @Override
    public List<Article> fetchArticles(FetchCommand cmd) {

        String encodedKeyword = URLEncoder.encode(cmd.keyword(), StandardCharsets.UTF_8);

        NaverArticleResponseDto response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/v1/search/news.json")
                .queryParam("query", encodedKeyword)
                .queryParam("display", cmd.pageSize())
                .queryParam("start", cmd.offset())
                .queryParam("sort", cmd.sortBy())
                .build()
            )
            .header("X-Naver-Client-Id", clientId)
            .header("X-Naver-Client-Secret", clientSecret)
            .retrieve()
            .bodyToMono(NaverArticleResponseDto.class)
            .block();

        if (response == null) {
            throw new IllegalArgumentException(); // TODO : 예외 상세화
        }

        return articleMapper.naverArticleListToEntity(response.items());
    }
}
