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
import reactor.util.retry.Retry;

import java.time.Duration;

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
            // 서버 측 에러 발생 시 재시도 하지 않고 예외 반환하도록
            // TODO: 제대로 동작하는지 테스트 필요
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            log.warn("WebClient 호출 도중 5xx 에러 발생: " + errorBody);
                            return Mono.error(new CustomException(ErrorCode.WEBCLIENT_ERROR, errorBody));
                        });
            }

            return Mono.just(clientResponse);
        });
    }
}
