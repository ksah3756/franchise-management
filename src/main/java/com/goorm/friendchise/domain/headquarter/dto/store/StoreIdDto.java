package com.goorm.friendchise.domain.headquarter.dto.store;

public record StoreIdDto(
        Long id
) {
    public static StoreIdDto of(Long id) {
        return new StoreIdDto(id);
    }
}
