package com.goorm.friendchise.domain.headquarter.Item.infrastructure;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaItemRepository extends JpaRepository<Item, Long> {
    Slice<Item> findByHeadquarterId(Long headquarterId, Pageable pageable);

    @Query(value = "SELECT * FROM item i WHERE i.headquarter_id = :headquarterId", nativeQuery = true)
    Slice<Item> findItemsByHeadquarterIdNative(@Param("headquarterId") Long headquarterId, Pageable pageable);
}
