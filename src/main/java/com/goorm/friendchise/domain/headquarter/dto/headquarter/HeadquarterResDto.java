package com.goorm.friendchise.domain.headquarter.dto.headquarter;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HeadquarterResDto(
    Long id,
    String franchiseName
) {
    public static HeadquarterResDto from(Headquarter headquarter) {
        return new HeadquarterResDto(headquarter.getId(), headquarter.getFranchiseName());
    }
}
