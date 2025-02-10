package com.goorm.friendchise.domain.headquarter.dto.kakaomap;

import lombok.Getter;

@Getter
public enum SearchCategory {
    BUS("버스정류장"),
    SUBWAY("지하철역"),
    // 대형마트, 학교, 주차장, 문화시설, 관광명소, 숙박, 병원, 약국
    MART("대형마트"),
    SCHOOL("학교"),
    PARKING("주차장"),
    CULTURE("문화시설"),
    TOUR("관광명소"),
    ACCOMMODATION("숙박"),
    HOSPITAL("병원"),
    PHARMACY("약국");
    private final String category;

    SearchCategory(String category) {
        this.category = category;
    }
}
