package com.goorm.api.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class TestWebClientConfig {
    @Bean
    @Primary
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://stub") // 실제 호출은 일어나지 않도록
                .build();
    }

    @Bean(name = "openAiWebClient")
    public WebClient openaiWebClient() {
        return WebClient.builder()
                .baseUrl("http://stub") // 실제 호출은 일어나지 않도록
                .build();
    }
}
