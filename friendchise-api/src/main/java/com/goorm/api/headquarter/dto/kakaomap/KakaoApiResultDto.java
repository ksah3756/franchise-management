package com.goorm.api.headquarter.dto.kakaomap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goorm.api.headquarter.dto.kakaomap.KakaoPlaceDto;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoApiResultDto(
        List<KakaoPlaceDto> documents
) {
}
