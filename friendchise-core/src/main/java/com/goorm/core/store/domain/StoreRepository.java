package com.goorm.core.store.domain;

import java.util.Optional;

public interface StoreRepository {
    Store save(Store store);

    Optional<Store> findById(Long id);

    void deleteById(Long id);
}
