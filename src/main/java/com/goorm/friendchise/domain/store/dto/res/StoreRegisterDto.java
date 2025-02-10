package com.goorm.friendchise.domain.store.dto.res;

public record StoreRegisterDto (
        String address,
        String dong,
        Double pointX,
        Double pointY,
        String franchiseName
) {}
