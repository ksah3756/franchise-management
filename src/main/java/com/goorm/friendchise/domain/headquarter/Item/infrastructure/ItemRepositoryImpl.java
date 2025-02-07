package com.goorm.friendchise.domain.headquarter.Item.infrastructure;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import com.goorm.friendchise.domain.headquarter.Item.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final JpaItemRepository jpaItemRepository;

    @Override
    public Item save(Item item) {
        return jpaItemRepository.save(item);
    }

    @Override
    public Optional<Item> findById(Long id) {
        return jpaItemRepository.findById(id);
    }

}
