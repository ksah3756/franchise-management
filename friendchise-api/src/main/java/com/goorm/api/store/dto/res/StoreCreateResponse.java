package com.goorm.api.store.dto.res;

import com.goorm.core.store.domain.Store;

public record StoreCreateResponse(
    Long id,
    String address,
    String dong,
    Double pointX,
    Double pointY,
    String franchiseName
) {
   public static StoreCreateResponse of(Long id, String address, String dong, Double pointX, Double pointY, String franchiseName) {
        return new StoreCreateResponse(id, address, dong, pointX, pointY, franchiseName);
    }

    public static StoreCreateResponse fromEntity(Store store) {
        return new StoreCreateResponse(store.getId(), store.getAddress(), store.getDong(), store.getPointX(), store.getPointY(), store.getFranchiseName());
    }
}
