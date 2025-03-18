package com.goorm.friendchise.domain.headquarter.dto.headquarter;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.goorm.friendchise.domain.headquarter.domain.RestaurantCategory;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.RestaurantSubCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HeadquarterRequest(
        @NotBlank(message = "프랜차이즈 이름은 필수입니다.")
        @Size(max = 50, message = "프랜차이즈 이름은 50자 이하로 입력해주세요.")
        String franchiseName,

        @NotBlank(message = "상위 카테고리는 필수입니다.")
        String category,

        @NotNull
        String subCategory
) {
    public static HeadquarterRequest of(String franchiseName, String category, String subCategory) {
        return new HeadquarterRequest(franchiseName, category, subCategory);
    }

    public static Headquarter toHeadquarter(HeadquarterRequest headquarterRequest) {
        return Headquarter.of(
                headquarterRequest.franchiseName(),
                RestaurantCategory.fromString(headquarterRequest.category),
                RestaurantSubCategory.fromString(headquarterRequest.subCategory));
    }
}
