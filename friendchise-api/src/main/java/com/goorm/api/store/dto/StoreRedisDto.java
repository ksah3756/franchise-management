package com.goorm.api.store.dto;

import lombok.Builder;

@Builder
public record StoreRedisDto(

        String address,
        String dong,
        Double pointX,
        Double pointY,
        String franchiseName,
        Long storeId,
        Long headquarterId
) {
}
