package com.goorm.friendchise.domain.headquarter.dto.headquarter;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.util.List;

/*
    가맹점 추천 요청 DTO
    gu(자치구), district(상권)이 null이 아니면 해당 정보를 이용하여 추천, null이면 x, y좌표를 이용하여 추천
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record StoreRecommendReqDto(
        List<String> userSelectedCategory,
        @DecimalMin(value = "124.0", message = "x좌표는 120.0보다 큰 값이어야 합니다.")
        @DecimalMax(value = "132.0", message = "x좌표는 132.0보다 작은 값이어야 합니다.")
        double x,
        @DecimalMin(value = "33.0", message = "y좌표는 33.0보다 큰 값이어야 합니다.")
        @DecimalMax(value = "43.0", message = "y좌표는 43.0보다 작은 값이어야 합니다.")
        double y
) {
}
