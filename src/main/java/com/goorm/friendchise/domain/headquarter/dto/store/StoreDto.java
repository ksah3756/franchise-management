package com.goorm.friendchise.domain.headquarter.dto.store;

public record StoreDto(
        Long id,
        String address,
        String dong,
        Double pointX,
        Double pointY,
        String franchiseName
) {
    public static StoreDto of(Long id, String address, String dong, Double pointX, Double pointY, String franchiseName) {
        return new StoreDto(id, address, dong, pointX, pointY, franchiseName);
    }
}
