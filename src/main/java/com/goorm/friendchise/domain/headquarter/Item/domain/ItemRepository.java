package com.goorm.friendchise.domain.headquarter.Item.domain;

import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);
    Optional<Item> findById(Long id);
}
