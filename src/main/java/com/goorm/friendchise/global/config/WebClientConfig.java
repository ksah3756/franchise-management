package com.goorm.friendchise.global.config;

import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfig {

    @Value("${KAKAO_API_KEY}")
    private String authorization;

    @Value("${OPENAI_API_KEY}")
    private String openaiAuthorization;

    @Bean
    @Primary
    public WebClient webClient() {
        String apiKey = "KakaoAK " + authorization;

        // TODO: Timeout 설정 필요
        return WebClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local")
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("Authorization", apiKey);
                    httpHeaders.set("X-Requested-With", "curl");
                })
                .filter(setExchangeFilterFunction())
                .build();
    }

    @Bean(name = "openAiWebClient")
    public WebClient openaiWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setBearerAuth(openaiAuthorization);
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .filter(setExchangeFilterFunction())
                .build();
    }


    private ExchangeFilterFunction setExchangeFilterFunction() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            // Log or process the 4xx error response
                            log.warn("4xx 에러 발생: " + errorBody);
                            return Mono.error(new CustomException(ErrorCode.WEBCLIENT_ERROR, errorBody));
                        });
            }

            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            // Log or process the 5xx error response
                            log.warn("5xx 에러 발생: " + errorBody);
                            return Mono.error(new CustomException(ErrorCode.WEBCLIENT_ERROR, errorBody));
                        });
            }

            return Mono.just(clientResponse);
        });
    }
}
