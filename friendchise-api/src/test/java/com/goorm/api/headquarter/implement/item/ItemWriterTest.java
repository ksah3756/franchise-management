package com.goorm.api.headquarter.implement.item;

import com.goorm.api.headquarter.dto.item.ItemRequest;
import com.goorm.api.headquarter.dto.item.ItemRequestList;
import com.goorm.api.headquarter.dto.item.ItemResponse;
import com.goorm.core.headquarter.domain.*;
import com.goorm.core.headquarter.infrastructure.HeadquarterRepositoryImpl;
import com.goorm.core.headquarter.infrastructure.ItemRepositoryImpl;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRepository;
import com.goorm.core.user.domain.UserRole;
import com.goorm.core.user.infrastructure.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = { // core 모듈의 domain package들을 scan
        "com.goorm.core.headquarter.domain",
        "com.goorm.core.store.domain",
        "com.goorm.core.user.domain"
})
@EnableJpaRepositories(basePackages = { // core 모듈의 infrastructure package들을 scan
        "com.goorm.core.headquarter.infrastructure",
        "com.goorm.core.store.infrastructure",
        "com.goorm.core.user.infrastructure"
})
@Import({ItemRepositoryImpl.class, HeadquarterRepositoryImpl.class, UserRepositoryImpl.class})
class ItemWriterTest {
    private ItemWriter itemWriter;

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemWriter = new ItemWriter(itemRepository);
    }

    private User createUser() {
        User user = User.create("test", "test", UserRole.HEADQUARTER);
        return userRepository.save(user);
    }

    @Test
    @DisplayName("Cascade persist로 다대일 양방향 연관관계인 Headquarter외 Item들이 모두 잘 저장되는지 확인")
    void testCreateItems_CascadePersist() {
        // given: Headquarter 생성 및 저장
        User user = createUser();
        Headquarter headquarter = Headquarter.builder()
                .userId(user.getId())
                .franchiseName("test")
                .restaurantCategory(RestaurantCategory.FASTFOOD)
                .restaurantSubCategory(RestaurantSubCategory.NONE)
                .build();

        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
        Long savedHeadquarterId = savedHeadquarter.getId();

        // given: ItemReqDtoList 생성 (예: 2개의 아이템 요청)
        List<ItemRequest> itemReqDtos = List.of(
                ItemRequest.of("item1", 1000),
                ItemRequest.of("item2", 2000)
        );
        ItemRequestList itemReqDtoList = new ItemRequestList(itemReqDtos);

        // when: createItems 메서드 호출
        List<ItemResponse> itemResDtos = itemWriter.createItems(savedHeadquarter, itemReqDtoList);

        // then: Headquarter와 연관된 Item이 DB에 저장되었는지 검증
        Headquarter foundHeadquarter = headquarterRepository.findById(savedHeadquarterId)
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
                .map(ItemResponse::name)
                .collect(Collectors.toList());
        assertThat(itemNames).containsExactlyInAnyOrder("item1", "item2");
    }
}