package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.FetchArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import com.codeit.team2.monew.module.domain.article.dto.NaverArticleItemDto;
import com.codeit.team2.monew.module.domain.article.dto.NaverArticleResponseDto;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class NaverNewsClient implements NewsClient {

    private WebClient webClient;
    private String clientId;
    private String clientSecret;

    public NaverNewsClient(
        @Qualifier("naverNewsClient") WebClient webClient,
        @Value(value = "${news.naver.client-id}") String clientId,
        @Value(value = "${news.naver.client-secret}") String clientSecret) {
        this.webClient = webClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public List<FetchArticleDto> fetchArticles(FetchCommand cmd) {

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

        List<FetchArticleDto> returnDto = new ArrayList<>();

        for (NaverArticleItemDto item : response.items()) {
            FetchArticleDto dto = new FetchArticleDto(
                item.title(),
                "NAVER",
                item.link(),
                item.description(),
                item.pubDate()
            );

            returnDto.add(dto);
        }

        return returnDto;
    }
}
