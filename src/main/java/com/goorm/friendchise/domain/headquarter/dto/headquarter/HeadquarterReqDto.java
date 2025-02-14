package com.goorm.friendchise.domain.headquarter.dto.headquarter;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.goorm.friendchise.domain.headquarter.domain.Category;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.SubCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HeadquarterReqDto (
        @NotBlank(message = "프랜차이즈 이름은 필수입니다.")
        @Size(max = 50, message = "프랜차이즈 이름은 50자 이하로 입력해주세요.")
        String franchiseName,

        @NotBlank(message = "상위 카테고리는 필수입니다.")
        String category,

        @NotNull
        String subCategory
) {
    public static HeadquarterReqDto of(String franchiseName, String category, String subCategory) {
        return new HeadquarterReqDto(franchiseName, category, subCategory);
    }

    public static Headquarter toEntity(HeadquarterReqDto headquarterReqDto) {
        return Headquarter.of(
                headquarterReqDto.franchiseName(),
                Category.fromString(headquarterReqDto.category),
                SubCategory.fromString(headquarterReqDto.subCategory));
    }
}
