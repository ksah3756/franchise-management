package com.goorm.friendchise.domain.headquarter.implement;

import reactor.core.publisher.Mono;

public interface LocalDataReader {
    // 동일 프랜차이즈 매장 검색
    String getSameFranchiseStore(String keyword, Double x, Double y, int radius);
    // 유사 업종 경쟁 매장 검색
    Mono<String> getCompetitiveStore(String keyword, Double x, Double y, int radius);
    // 버스 정류장 검색
    Mono<String> getBusStation(String keyword, Double x, Double y, int radius);
    // 지하철역 검색
    Mono<String> getSubwayStation(String keyword, Double x, Double y, int radius);
    // 사용자가 선택한 카테고리로 검색
    Mono<String> getUserSelectedInfra(String category, Double x, Double y, int radius);
}
