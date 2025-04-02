package com.goorm.api.headquarter.dto.headquarter;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.RestaurantCategory;
import com.goorm.core.headquarter.domain.RestaurantSubCategory;
import com.goorm.core.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HeadquarterCreateRequest(
        @NotBlank(message = "프랜차이즈 이름은 필수입니다.")
        @Size(max = 50, message = "프랜차이즈 이름은 50자 이하로 입력해주세요.")
        String franchiseName,

        @NotBlank(message = "상위 카테고리는 필수입니다.")
        String category,

        @NotNull
        String subCategory
) {
    public static HeadquarterCreateRequest of(String franchiseName, String category, String subCategory) {
        return new HeadquarterCreateRequest(franchiseName, category, subCategory);
    }

    public static Headquarter toEntity(Long userId, HeadquarterCreateRequest headquarterCreateRequest) {
        return Headquarter.create(
                userId,
                headquarterCreateRequest.franchiseName(),
                RestaurantCategory.fromString(headquarterCreateRequest.category),
                RestaurantSubCategory.fromString(headquarterCreateRequest.subCategory));
    }
}
