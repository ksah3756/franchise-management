package com.goorm.friendchise.domain.headquarter.Item.application;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import com.goorm.friendchise.domain.headquarter.Item.domain.ItemRepository;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDto;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDtoList;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemResDto;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.global.auth.application.AuthService;
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
    private final AuthService authService;

    private Manager getCurrentManager(){
        return authService.findManagerByAuth();
    }

    @Transactional
    public List<ItemResDto> createItems(ItemReqDtoList itemReqDtoList) {
        Headquarter headquarter = getCurrentHeadquarter();

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
    public Slice<ItemResDto> getItems(Pageable pageable) {
        Headquarter headquarter = getCurrentHeadquarter();

        // headquarter.id = headquarterId 이런식으로 조회되기 때문에 페치 조인이 아님에도 left join해서 headquarter까지 가져옮
        return itemRepository.findByHeadquarterId(headquarter.getId(), pageable).map(ItemResDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public Slice<ItemResDto> getItemsNative(Pageable pageable) {
        Headquarter headquarter = getCurrentHeadquarter();

        // native query로 불필요한 left join X
        return itemRepository.findItemsByHeadquarterIdNative(headquarter.getId(), pageable).map(ItemResDto::fromEntity);
    }

    private Headquarter getCurrentHeadquarter() {
        Manager currentManager = getCurrentManager();
        return findHeadquarterById(currentManager);
    }

    private Headquarter findHeadquarterById(Manager currentManager) {
        if (currentManager.getManageId() == null) {
            throw new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND);
        }

        return headquarterRepository.findById(currentManager.getManageId())
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }
}
