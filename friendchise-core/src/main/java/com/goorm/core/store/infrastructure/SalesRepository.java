package com.goorm.core.store.infrastructure;


import com.goorm.core.store.domain.Sales;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Long> {

    List<Sales> findAllByStoreId(Long storeId, Pageable pageable);
}
