package com.goorm.friendchise.domain.headquarter.dto.kakaomap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoRegionDto(
        String regionType,
        @JsonProperty("region_2depth_name")
        String guName,
        @JsonProperty("region_3depth_name")
        String hDongName
) {
}
