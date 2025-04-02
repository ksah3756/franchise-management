package com.goorm.api.headquarter.implement.map;

import reactor.core.publisher.Mono;

// 만약 새로운 기능(조건)이 계속 추가되는 상황이라면 인터페이스의 구현체가 전부 수정되어야 하므로 좋지 않은 설계가 될 수 있다..
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
