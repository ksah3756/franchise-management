package com.goorm.friendchise.domain.headquarter.Item.infrastructure;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import com.goorm.friendchise.domain.headquarter.Item.domain.ItemRepository;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Override
    public Slice<Item> findByHeadquarterId(Long headquarterId, Pageable pageable) {
        return jpaItemRepository.findByHeadquarterId(headquarterId, pageable);
    }

    @Override
    public Slice<Item> findItemsByHeadquarterIdNative(Long headquarterId, Pageable pageable) {
        return jpaItemRepository.findItemsByHeadquarterIdNative(headquarterId, pageable);
    }
}
