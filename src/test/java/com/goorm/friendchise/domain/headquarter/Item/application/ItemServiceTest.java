package com.goorm.friendchise.domain.headquarter.Item.application;

import com.goorm.friendchise.domain.headquarter.Item.domain.ItemRepository;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDto;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDtoList;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemResDto;
import com.goorm.friendchise.domain.headquarter.appilcation.HeadquarterService;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.infrastructure.HeadquarterRepositoryImpl;
import com.goorm.friendchise.domain.headquarter.insfrastructure.FakeHeadquarterRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = "com.goorm.friendchise.domain.headquarter")
@EnableJpaRepositories(basePackages = "com.goorm.friendchise.domain.headquarter")
@Import({ItemService.class, HeadquarterRepositoryImpl.class})
class ItemServiceTest {

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @Autowired
    private ItemService itemService;

    @Test
    @DisplayName("Cascade persist로 다대일 양방향 연관관계인 Headquarter외 Item들이 모두 잘 저장되는지 확인")
    void testCreateItems_CascadePersist() {
        // given: Headquarter 생성 및 저장
        Headquarter headquarter = Headquarter.builder()
                .franchiseName("testHeadquarter")
                .items(new ArrayList<>())
                .build();
        headquarterRepository.save(headquarter);

        // given: ItemReqDtoList 생성 (예: 2개의 아이템 요청)
        List<ItemReqDto> itemReqDtos = List.of(
                ItemReqDto.of("item1", 1000),
                ItemReqDto.of("item2", 2000)
        );
        ItemReqDtoList itemReqDtoList = new ItemReqDtoList(itemReqDtos);

        // when: createItems 메서드 호출
        List<ItemResDto> itemResDtos = itemService.createItems(headquarter.getId(), itemReqDtoList);

        // then: Headquarter와 연관된 Item이 DB에 저장되었는지 검증
        Headquarter foundHeadquarter = headquarterRepository.findById(headquarter.getId())
                .orElseThrow(() -> new RuntimeException("Headquarter not found"));

        // Headquarter의 items 컬렉션에 2개의 Item이 저장되어 있는지 확인
        assertThat(foundHeadquarter.getItems()).hasSize(2);

        // 각 Item의 headquarter 필드가 올바르게 설정되었는지 확인
        foundHeadquarter.getItems().forEach(item ->
                assertThat(item.getHeadquarter()).isEqualTo(foundHeadquarter)
        );

        // 결과 DTO 검증
        assertThat(itemResDtos).hasSize(2);
        List<String> itemNames = itemResDtos.stream()
                .map(ItemResDto::name)
                .collect(Collectors.toList());
        assertThat(itemNames).containsExactlyInAnyOrder("item1", "item2");
    }
}