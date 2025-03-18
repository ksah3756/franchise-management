package com.goorm.friendchise.domain.headquarter.business;

import com.goorm.friendchise.domain.headquarter.domain.Item;
import com.goorm.friendchise.domain.headquarter.domain.ItemRepository;
import com.goorm.friendchise.domain.headquarter.dto.item.ItemReqDto;
import com.goorm.friendchise.domain.headquarter.dto.item.ItemReqDtoList;
import com.goorm.friendchise.domain.headquarter.dto.item.ItemResDto;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final HeadquarterRepository headquarterRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public List<ItemResDto> createItems(Manager currentManager, ItemReqDtoList itemReqDtoList) {
        Headquarter headquarter = getCurrentHeadquarter(currentManager);

        // 2. 들어온 요청 DTO 리스트를 순회하며 Item 생성 후 연관관계 설정
        List<ItemResDto> savedItemDtoList = new ArrayList<>();
        for (ItemReqDto itemReqDto : itemReqDtoList.itemList()) {
            Item item = ItemReqDto.toEntity(itemReqDto);
            // 연관관계 편의 메서드를 통해 양쪽 세팅
            headquarter.addItem(item);
            savedItemDtoList.add(ItemResDto.fromEntity(item));
        }

        return savedItemDtoList;
    }

    @Transactional(readOnly = true)
    public Slice<ItemResDto> getItems(Manager currentManager, Pageable pageable) {
        Headquarter headquarter = getCurrentHeadquarter(currentManager);

        // headquarterId로 조회 시 headquarter.id를 조회하면서 left join 발생
        return itemRepository.findByHeadquarter(headquarter.getId(), pageable).map(ItemResDto::fromEntity);
    }


    private Headquarter getCurrentHeadquarter(Manager currentManager) {
        return getHeadquarterById(currentManager);
    }

    private Headquarter getHeadquarterById(Manager currentManager) {
        if (currentManager.getManageId() == null) {
            throw new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND);
        }

        return headquarterRepository.findById(currentManager.getManageId())
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }
}
