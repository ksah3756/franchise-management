package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class KakaoApiClient {
    private final WebClient webClient;

    public KakaoApiResultDto requestPlaceDataByKeywordSync(String keyword, Double y, Double x, int radius) {
        String uri = makeKeywordSearchAPIUri(keyword, y, x, radius);

        return getKakaoApiResultDtoMono(uri).block();
    }

    public Mono<KakaoApiResultDto> requestPlaceDataByKeywordAsync(String keyword, Double y, Double x, int radius) {
        String uri = makeKeywordSearchAPIUri(keyword, y, x, radius);

        return getKakaoApiResultDtoMono(uri);
    }

    public KakaoApiResultDto requestPlaceDataByCategorySync(String categoryGroupCode, Double y, Double x, int radius) {
        String uri = makeCategorySearchAPIUri(categoryGroupCode, y, x, radius);

        return getKakaoApiResultDtoMono(uri).block();
    }

    public Mono<KakaoApiResultDto> requestPlaceDataByCategoryAsync(String categoryGroupCode, Double y, Double x, int radius) {
        String uri = makeCategorySearchAPIUri(categoryGroupCode, y, x, radius);

        return getKakaoApiResultDtoMono(uri);
    }

    // 키워드로 장소 검색하기 API URI 생성

    private String makeKeywordSearchAPIUri(String keyword, Double y, Double x, int radius) {
        return UriComponentsBuilder.fromPath("/search/keyword.json")
                .queryParam("query", keyword)
                .queryParam("x", x)
                .queryParam("y", y)
                .queryParam("radius", radius) // 값 조정 필요
                .queryParam("size", 10)
                .queryParam("sort", "distance")
                .build()
                .toUriString();
    }
    // 카테고리로 장소 검색하기 API URI 생성

    private String makeCategorySearchAPIUri(String categoryGroupCode, Double y, Double x, int radius) {
        return UriComponentsBuilder.fromPath("/search/category.json")
                .queryParam("category_group_code", categoryGroupCode)
                .queryParam("x", x)
                .queryParam("y", y)
                .queryParam("radius", radius) // 값 조정 필요
                .queryParam("size", 10)
                .queryParam("sort", "distance")
                .build()
                .toUriString();
    }

    private Mono<KakaoApiResultDto> getKakaoApiResultDtoMono(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new CustomException(ErrorCode.WEBCLIENT_ERROR)))
                .bodyToMono(KakaoApiResultDto.class)
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(200))
                        .filter(throwable -> throwable instanceof CustomException) // 4xx 에러의 경우 재시도 X
                        .jitter(0.5));
    }
}
