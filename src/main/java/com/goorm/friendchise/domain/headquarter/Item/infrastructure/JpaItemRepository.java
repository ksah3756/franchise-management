package com.goorm.friendchise.domain.headquarter.Item.infrastructure;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItemRepository extends JpaRepository<Item, Long> {
}
