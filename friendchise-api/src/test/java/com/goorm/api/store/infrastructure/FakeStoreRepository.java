package com.goorm.api.store.infrastructure;

import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.store.domain.Store;
import com.goorm.core.store.domain.StoreRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FakeStoreRepository implements StoreRepository {
    private final List<Store> stores = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public Store save(Store store) {
        Store savedStore = Store.builder()
                .id(sequence.getAndIncrement())
                .user(store.getUser())
                .address(store.getAddress())
                .dong(store.getDong())
                .pointX(store.getPointX())
                .pointY(store.getPointY())
                .franchiseName(store.getFranchiseName())
                .headquarterId(store.getHeadquarterId())
                .build();

        stores.add(savedStore);
        return savedStore;
    }

    @Override
    public Optional<Store> findById(Long id) {
        return stores.stream()
                .filter(store -> store.getId().equals(id))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        stores.removeIf(store -> store.getId().equals(id));
    }

}
