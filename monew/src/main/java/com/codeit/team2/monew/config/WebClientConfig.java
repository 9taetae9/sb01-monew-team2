package com.codeit.team2.monew.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {


    /**
     * 네이버 뉴스 WebClient Bean
     *
     * @param url application.yml 에서 주입받는 baseUrl
     */
    @Bean
    @Qualifier("naverApiNewsClient")
    public WebClient naverWebClient(@Value("${news.naver.url}") String url) {
        return WebClient.builder().baseUrl(url).build();
    }

    @Bean
    @Qualifier("redirectClient")
    public WebClient webClientWithRedirect() {
        HttpClient httpClient = HttpClient.create().followRedirect(true);

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(
                ExchangeStrategies.builder().codecs(configurer -> configurer.defaultCodecs()
                    .maxInMemorySize(2 * 1024 * 1024)).build())
            .build();
    }
}
