package com.goorm.friendchise.domain.headquarter.dto.headquarter;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record HeadquarterResponse(
    Long id,
    String franchiseName
) {
    public static HeadquarterResponse from(Headquarter headquarter) {
        return new HeadquarterResponse(headquarter.getId(), headquarter.getFranchiseName());
    }
}
