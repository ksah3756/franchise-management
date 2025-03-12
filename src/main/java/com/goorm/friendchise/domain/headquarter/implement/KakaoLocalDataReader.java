package com.goorm.friendchise.domain.headquarter.implement;

import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoApiResultDto;
import com.goorm.friendchise.domain.headquarter.dto.kakaomap.KakaoPlaceDto;
import com.goorm.friendchise.global.common.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KakaoLocalDataReader implements LocalDataReader {
    private final WebClient webClient;

    @Override
    public String getSameFranchiseStore(String keyword, Double x, Double y, int radius) {
        return requestPlaceDataByKeywordSync(keyword, x, y, 500)
                .documents()
                .stream()
                .map(KakaoPlaceDto::distance)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    @Override
    public Mono<String> getCompetitiveStore(String keyword, Double x, Double y, int radius) {
        return requestPlaceDataByKeywordAsync(keyword, x, y, 1000)
                .map(apiResult -> {
                    List<KakaoPlaceDto> documents = apiResult.documents();
                    return documents.stream()
                            .map(KakaoPlaceDto::distance)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "));
                });
    }

    @Override
    public Mono<String> getBusStation(String keyword, Double x, Double y, int radius) {
        return requestPlaceDataByKeywordAsync(keyword, x, y, 200)
                .map(apiResult -> {
                    List<KakaoPlaceDto> documents = apiResult.documents();
                    return documents.stream()
                            .map(KakaoPlaceDto::distance)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "));
                });
    }

    @Override
    public Mono<String> getSubwayStation(String keyword, Double x, Double y, int radius) {
        return requestPlaceDataByCategoryAsync(keyword, x, y, 500)
                .map(apiResult -> {
                    List<KakaoPlaceDto> documents = apiResult.documents();
                    return documents.stream()
                            .map(KakaoPlaceDto::distance)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "));
                });
    }

    @Override
    public Mono<String> getUserSelectedInfra(String category, Double x, Double y, int radius) {
        return requestPlaceDataByCategoryAsync(category, x, y, 500)
                .map(apiResult -> {
                    List<KakaoPlaceDto> documents = apiResult.documents();
                    return documents.stream()
                            .map(KakaoPlaceDto::distance)
                            .map(String::valueOf)
                            .collect(Collectors.joining(", "));
                });
    }

    private KakaoApiResultDto requestPlaceDataByKeywordSync(String keyword, Double x, Double y, int radius) {
        String uri = makeKeywordSearchAPIUri(keyword, x, y, radius);

        return getKakaoApiResultDtoMono(uri).block();
    }

    private Mono<KakaoApiResultDto> requestPlaceDataByKeywordAsync(String keyword, Double x, Double y, int radius) {
        String uri = makeKeywordSearchAPIUri(keyword, x, y, radius);

        return getKakaoApiResultDtoMono(uri);
    }

    private KakaoApiResultDto requestPlaceDataByCategorySync(String categoryGroupCode, Double x, Double y, int radius) {
        String uri = makeCategorySearchAPIUri(categoryGroupCode, x, y, radius);

        return getKakaoApiResultDtoMono(uri).block();
    }

    private Mono<KakaoApiResultDto> requestPlaceDataByCategoryAsync(String categoryGroupCode, Double x, Double y, int radius) {
        String uri = makeCategorySearchAPIUri(categoryGroupCode, x, y, radius);

        return getKakaoApiResultDtoMono(uri);
    }

    // 키워드로 장소 검색하기 API URI 생성
    private String makeKeywordSearchAPIUri(String keyword, Double x, Double y, int radius) {
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

    private String makeCategorySearchAPIUri(String categoryGroupCode, Double x, Double y, int radius) {
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
        Mono<KakaoApiResultDto> kakaoApiResultDtoMono = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(KakaoApiResultDto.class);
        return WebClientUtil.applyRetryAndTimeout(kakaoApiResultDtoMono);

    }
}
