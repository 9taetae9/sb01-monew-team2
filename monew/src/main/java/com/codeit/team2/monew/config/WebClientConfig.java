package com.codeit.team2.monew.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


    /**
     * 네이버 뉴스 WebClient Bean
     *
     * @param url application.yml 에서 주입받는 baseUrl
     */
    @Bean
    @Qualifier("naverNewsClient")
    public WebClient naverWebClient(@Value("${news.naver.url}") String url) {
        return WebClient.builder().baseUrl(url).build();
    }
}
