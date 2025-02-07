package com.goorm.friendchise.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${kakao.apiKey}")
    private String authorization;

    @Bean
    public WebClient webClient() {
        String apiKey = "KakaoAK " + authorization;

        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local")
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Authorization", apiKey);
                    httpHeaders.set("X-Requested-With", "curl");
                })
                .build();
    }
}
