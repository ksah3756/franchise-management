package com.goorm.api.store.dto;


import com.goorm.core.store.domain.Store;

public record StoreResDto(
        Long id,
        String address,
        String dong,
        Double pointX,
        Double pointY,
        String franchiseName
) {
    public static StoreResDto fromEntity(Store store) {
        return new StoreResDto(store.getId(), store.getAddress(), store.getDong(), store.getPointX(), store.getPointY(), store.getFranchiseName());
    }
}
