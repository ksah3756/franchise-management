package com.goorm.friendchise.domain.headquarter.Item.application;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDto;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDtoList;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemResDto;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final HeadquarterRepository headquarterRepository;

    @Transactional
    public List<ItemResDto> createItems(Long headquarterId, ItemReqDtoList itemReqDtoList) {
        // 1. Headquarter 조회 (존재하지 않으면 예외 처리)
        Headquarter headquarter = findHeadquarterById(headquarterId);

        // 2. 들어온 요청 DTO 리스트를 순회하며 Item 생성 후 연관관계 설정
        List<Item> itemsToSave = new ArrayList<>();
        for (ItemReqDto itemReqDto : itemReqDtoList.itemList()) {
            Item item = ItemReqDto.toEntity(itemReqDto);
            // 연관관계 편의 메서드를 통해 양쪽 세팅
            headquarter.addItem(item);
            itemsToSave.add(item);
        }

        // 3. 결과 변환 후 리턴
        return itemsToSave.stream()
                .map(ItemResDto::fromEntity)
                .toList();
    }

    private Headquarter findHeadquarterById(Long headquarterId) {
        return headquarterRepository.findById(headquarterId)
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }
}
