package com.goorm.core.store.infrastructure;

import com.goorm.core.store.domain.Store;
import com.goorm.core.store.domain.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {
    private final JpaStoreRepository jpaStoreRepository;
    @Override
    public Store save(Store store) {
        return jpaStoreRepository.save(store);
    }

    @Override
    public Optional<Store> findById(Long id) {
        return jpaStoreRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaStoreRepository.deleteById(id);
    }

    @Override
    public Optional<Store> findByUserId(Long userId) {
        return jpaStoreRepository.findByUserId(userId);
    }

    @Override
    public void deleteAll() {
        jpaStoreRepository.deleteAll();
    }
}
