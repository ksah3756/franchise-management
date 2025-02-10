package com.goorm.friendchise.domain.headquarter.dto.kakaomap;

import java.util.List;

public record KakaoApiResultDto(
        List<KakaoPlaceDto> documents
) {
}
