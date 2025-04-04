package com.goorm.api.store.dto.res;

public record AddressDetailDto(
        String address,
        String roadAddress,
        String zoneNumber,
        String dong,
        Double x,
        Double y
) {


}
