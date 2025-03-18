package com.goorm.friendchise.domain.headquarter.dto.kakaomap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoApiResultDto(
        List<KakaoPlaceDto> documents
) {
}
