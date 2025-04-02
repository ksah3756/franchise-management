package com.goorm.api.headquarter.implement.item;

import com.goorm.api.headquarter.dto.item.ItemResponse;
import com.goorm.api.headquarter.implement.headquarter.HeadquarterReader;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.headquarter.domain.*;
import com.goorm.core.headquarter.infrastructure.HeadquarterRepositoryImpl;
import com.goorm.core.headquarter.infrastructure.ItemRepositoryImpl;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRepository;
import com.goorm.core.user.domain.UserRole;
import com.goorm.core.user.infrastructure.UserRepositoryImpl;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
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
@Import({ItemReader.class, ItemRepositoryImpl.class, HeadquarterRepositoryImpl.class, UserRepositoryImpl.class})
class ItemReaderTest {

    @Autowired
    private ItemReader itemReader;

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User createUser() {
        User user = User.create("test", "test", UserRole.HEADQUARTER);
        return userRepository.save(user);
    }

    @Test
    @DisplayName("본사의 메뉴 목록을 조회한다.")
    void getItems() {
        // given
        User user = createUser();
        Headquarter headquarter = Headquarter.builder()
                .franchiseName("test")
                .restaurantCategory(RestaurantCategory.FASTFOOD)
                .restaurantSubCategory(RestaurantSubCategory.NONE)
                .user(user)
                .build();

        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
        Long savedHeadquarterId = savedHeadquarter.getId();

        Headquarter foundHeadquarter = headquarterRepository.findById(savedHeadquarterId)
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
        List<ItemResponse> savedItemResDtos = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Item item = Item.builder()
                    .name("item" + i)
                    .price(1000)
                    .headquarter(foundHeadquarter)
                    .build();
            Item savedItem = itemRepository.save(item);
            savedItemResDtos.add(ItemResponse.fromEntity(savedItem));
        }

        // when
        Slice<ItemResponse> itemResDtos1 = itemReader.getItems(foundHeadquarter, PageRequest.of(0,5));
        Slice<ItemResponse> itemResDtos2 = itemReader.getItems(foundHeadquarter, PageRequest.of(1,5));

        // then
        assertThat(itemResDtos1).hasSize(5);
        List<String> itemNames = itemResDtos1.stream()
                .map(ItemResponse::name)
                .collect(Collectors.toList());
        assertThat(itemNames).containsExactlyInAnyOrder("item1", "item2", "item3", "item4", "item5");

        assertThat(itemResDtos2).hasSize(5);
        itemNames = itemResDtos2.stream()
                .map(ItemResponse::name)
                .collect(Collectors.toList());
        assertThat(itemNames).containsExactlyInAnyOrder("item6", "item7", "item8", "item9", "item10");
    }
}