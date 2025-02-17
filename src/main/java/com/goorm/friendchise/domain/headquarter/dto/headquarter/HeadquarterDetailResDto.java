package com.goorm.friendchise.domain.headquarter.dto.headquarter;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;

public record HeadquarterDetailResDto(
        Long id,
        String franchiseName,
        String category,
        String subCategory
) {
    public static HeadquarterDetailResDto from(Headquarter headquarter) {
        return new HeadquarterDetailResDto(headquarter.getId(), headquarter.getFranchiseName(), headquarter.getCategory().getValue(), headquarter.getSubCategory().getValue());
    }
}
