package com.goorm.friendchise.domain.headquarter.application;

import reactor.core.publisher.Mono;

import java.util.List;

public interface MapApiClient {
    // 동일 프랜차이즈 매장 검색
    String searchSameFranchiseStore(String keyword, Double x, Double y, int radius);
    // 유사 업종 경쟁 매장 검색
    Mono<String> searchCompetitiveStore(String keyword, Double x, Double y, int radius);
    // 버스 정류장 검색
    Mono<String> searchBusStation(String keyword, Double x, Double y, int radius);
    // 지하철역 검색
    Mono<String> searchSubwayStation(String keyword,Double x, Double y, int radius);
    // 사용자가 선택한 카테고리로 검색
    Mono<String> searchUserSelectedInfra(String category, Double x, Double y, int radius);
}
