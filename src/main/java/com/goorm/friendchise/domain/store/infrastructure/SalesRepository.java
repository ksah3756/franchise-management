package com.goorm.friendchise.domain.store.infrastructure;

import com.goorm.friendchise.domain.store.domain.Sales;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SalesRepository extends MongoRepository<Sales, String> {
}
