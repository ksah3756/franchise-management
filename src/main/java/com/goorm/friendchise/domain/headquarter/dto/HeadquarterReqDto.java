package com.goorm.friendchise.domain.headquarter.dto;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HeadquarterReqDto (
        @NotBlank @Size(min = 1, max = 50) String franchiseName
) {
    public static HeadquarterReqDto of(String franchiseName) {
        return new HeadquarterReqDto(franchiseName);
    }

    public static Headquarter toEntity(HeadquarterReqDto headquarterReqDto) {
        return Headquarter.of(headquarterReqDto.franchiseName());
    }
}
