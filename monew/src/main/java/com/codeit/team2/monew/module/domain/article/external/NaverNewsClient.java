package com.codeit.team2.monew.module.domain.article.external;

import com.codeit.team2.monew.module.domain.article.dto.FetchArticleDto;
import com.codeit.team2.monew.module.domain.article.dto.FetchCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;


public class NaverNewsClient implements NewsClient {

    private WebClient webClient;
    private String clientId;
    private String clientSecret;

    public NaverNewsClient(WebClient webClient,
        @Value(value = "${news.naver.client-id}") String clientId,
        @Value(value = "${news.naver.client-secret}") String clientSecret) {
        this.webClient = webClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public List<FetchArticleDto> fetchArticles(FetchCommand cmd) {



        return null;
    }


}
