package com.goorm.friendchise.domain.headquarter.dto.kakaomap;

import lombok.Getter;

@Getter
public enum CategoryGroupCode {
    SUBWAY("지하철역", "SW8"),
    // 대형마트, 학교, 주차장, 문화시설, 관광명소, 숙박, 병원, 약국
    MART("대형마트", "MT1"),
    SCHOOL("학교", "SC4"),
    PARKING("주차장", "PK6"),
    CULTURE("문화시설", "CT1"),
    TOUR("관광명소", "AT4"),
    RESTAURANT("음식점", "FD6"),
    CAFE("카페", "CE7"),
    ACCOMMODATION("숙박", "AD5"),
    HOSPITAL("병원", "HP8"),
    PHARMACY("약국", "PM9");

    private final String value;
    private final String code;

    CategoryGroupCode(String value, String code) {
        this.value = value;
        this.code = code;
    }

    public static CategoryGroupCode fromString(String value) {
        for (CategoryGroupCode categoryGroupCode : CategoryGroupCode.values()) {
            if (categoryGroupCode.getValue().equalsIgnoreCase(value)) {
                return categoryGroupCode;
            }
        }
        return null;
    }
}
