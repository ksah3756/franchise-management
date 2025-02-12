package com.goorm.friendchise.domain.store.dto;

import lombok.Builder;

@Builder
public record StoreReqDto(
        String address,
        String roadAddress,
        String zoneNumber,
        String dong,
        Double x,
        Double y,
        String franchiseName,
        String headQuarterName
) {

}
