package com.goorm.friendchise.domain.headquarter.dto;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;

public record HeadquarterResDto(
    Long id,
    String franchiseName
) {
    public static HeadquarterResDto from(Headquarter headquarter) {
        return new HeadquarterResDto(headquarter.getId(), headquarter.getFranchiseName());
    }
}
