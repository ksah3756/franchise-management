package com.goorm.api.headquarter.implement.item;

import com.goorm.api.headquarter.dto.item.ItemResponse;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemReader {
    private final ItemRepository itemRepository;

    // TODO: 커서 기반 페이징 확인
    public Slice<ItemResponse> getItems(Headquarter headquarter, Pageable pageable) {
        // headquarterId로 조회 시 headquarter.id를 조회하면서 left join 발생
        return itemRepository.findByHeadquarter(headquarter.getId(), pageable).map(ItemResponse::fromEntity);
    }
}
