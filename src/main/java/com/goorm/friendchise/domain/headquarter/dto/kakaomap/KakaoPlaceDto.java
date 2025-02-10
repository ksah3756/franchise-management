package com.goorm.friendchise.domain.headquarter.dto.kakaomap;

public record KakaoPlaceDto(
        String placeName,
        String addressName,
        String distance,
        String categoryGroupName,
        String x,
        String y
) {
}
