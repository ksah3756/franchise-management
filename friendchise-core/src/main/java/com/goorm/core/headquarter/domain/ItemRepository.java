package com.goorm.core.headquarter.domain;

import com.goorm.core.headquarter.domain.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);
    Optional<Item> findById(Long id);
    Slice<Item> findByHeadquarter(Long headquarterId, Pageable pageable);
    Slice<Item> findItemsByHeadquarterIdNative(Long headquarterId, Pageable pageable);
}
