package com.goorm.api.headquarter.implement.item;

import com.goorm.api.headquarter.dto.item.ItemRequest;
import com.goorm.api.headquarter.dto.item.ItemRequestList;
import com.goorm.api.headquarter.dto.item.ItemResponse;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.Item;
import com.goorm.core.headquarter.domain.ItemRepository;
import com.goorm.core.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemWriter {
    private final ItemRepository itemRepository;

    public List<ItemResponse> createItems(Headquarter headquarter, ItemRequestList itemRequestList) {
        // 들어온 요청 DTO 리스트를 순회하며 Item 생성 후 연관관계 설정
        List<ItemResponse> savedItemDtoList = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequestList.itemList()) {
            Item item = ItemRequest.toEntity(itemRequest);
            // 연관관계 편의 메서드를 통해 양쪽 세팅
            headquarter.addItem(item);
            savedItemDtoList.add(ItemResponse.fromEntity(item));
        }

        return savedItemDtoList;
    }
}
