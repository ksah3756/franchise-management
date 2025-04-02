package com.goorm.core.store.infrastructure;

import com.goorm.core.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaStoreRepository extends JpaRepository<Store, Long> {

}
