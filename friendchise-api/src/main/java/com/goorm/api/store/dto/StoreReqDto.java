package com.goorm.api.store.dto;

import lombok.Builder;

@Builder
public record StoreReqDto(
        String name,
        String address,
        String roadAddress,
        String zoneNumber,
        String dong,
        Double x,
        Double y,
        String franchiseName,
        String certificationNumber
) {

}
