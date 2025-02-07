package com.goorm.friendchise.domain.store.infrastructure;

import com.goorm.friendchise.domain.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
