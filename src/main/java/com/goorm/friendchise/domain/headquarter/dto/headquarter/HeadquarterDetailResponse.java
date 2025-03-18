package com.goorm.friendchise.domain.headquarter.dto.headquarter;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;

public record HeadquarterDetailResponse(
        Long id,
        String franchiseName,
        String category,
        String subCategory
) {
    public static HeadquarterDetailResponse from(Headquarter headquarter) {
        return new HeadquarterDetailResponse(headquarter.getId(), headquarter.getFranchiseName(), headquarter.getRestaurantCategory().getValue(), headquarter.getRestaurantSubCategory().getValue());
    }
}
