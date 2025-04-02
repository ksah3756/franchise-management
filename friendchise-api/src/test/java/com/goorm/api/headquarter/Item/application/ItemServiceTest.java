//package com.goorm.api.headquarter.Item.application;
//
//import com.goorm.api.headquarter.application.HeadquarterService;
//import com.goorm.api.headquarter.dto.item.ItemRequest;
//import com.goorm.api.headquarter.dto.item.ItemRequestList;
//import com.goorm.api.headquarter.dto.item.ItemResponse;
//import com.goorm.core.common.exception.CustomException;
//import com.goorm.core.common.exception.ErrorCode;
//import com.goorm.core.headquarter.domain.*;
//import com.goorm.core.headquarter.infrastructure.HeadquarterRepositoryImpl;
//import com.goorm.core.headquarter.infrastructure.ItemRepositoryImpl;
//import com.goorm.core.store.domain.StoreRepository;
//import com.goorm.core.user.domain.User;
//import com.goorm.core.user.domain.UserRepository;
//import com.goorm.core.user.domain.UserRole;
//import com.goorm.core.user.infrastructure.UserRepositoryImpl;
//import org.apache.catalina.Manager;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@EntityScan(basePackages = { // core 모듈의 domain package들을 scan
//        "com.goorm.core.headquarter.domain",
//        "com.goorm.core.store.domain",
//        "com.goorm.core.user.domain"
//})
//@EnableJpaRepositories(basePackages = { // core 모듈의 infrastructure package들을 scan
//        "com.goorm.core.headquarter.infrastructure",
//        "com.goorm.core.store.infrastructure",
//        "com.goorm.core.user.infrastructure"
//})
//@Import({HeadquarterRepositoryImpl.class, ItemRepositoryImpl.class, UserRepositoryImpl.class})
//class ItemServiceTest {
//
//    @Autowired
//    private HeadquarterRepository headquarterRepository;
//
//    @Autowired
//    private HeadquarterService headquarterService;
//
//    @Autowired
//    private ItemRepository itemRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private User createUser() {
//        User user = User.create("test", "test", UserRole.HEADQUARTER);
//        return userRepository.save(user);
//    }
//
//    @Test
//    @DisplayName("Cascade persist로 다대일 양방향 연관관계인 Headquarter외 Item들이 모두 잘 저장되는지 확인")
//    void testCreateItems_CascadePersist() {
//        // given: Headquarter 생성 및 저장
//        User user = createUser();
//        Headquarter headquarter = Headquarter.builder()
//                .franchiseName("test")
//                .restaurantCategory(RestaurantCategory.FASTFOOD)
//                .restaurantSubCategory(RestaurantSubCategory.NONE)
//                .user(user)
//                .build();
//
//        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
//        Long savedHeadquarterId = savedHeadquarter.getId();
//
//        // given: ItemReqDtoList 생성 (예: 2개의 아이템 요청)
//        List<ItemRequest> itemReqDtos = List.of(
//                ItemRequest.of("item1", 1000),
//                ItemRequest.of("item2", 2000)
//        );
//        ItemRequestList itemReqDtoList = new ItemRequestList(itemReqDtos);
//
//        // when: createItems 메서드 호출
//        List<ItemResponse> itemResDtos = headquarterService.createItems(user, itemReqDtoList);
//
//        // then: Headquarter와 연관된 Item이 DB에 저장되었는지 검증
//        Headquarter foundHeadquarter = headquarterRepository.findById(savedHeadquarterId)
//                .orElseThrow(() -> new RuntimeException("Headquarter not found"));
//
//        // Headquarter의 items 컬렉션에 2개의 Item이 저장되어 있는지 확인
//        assertThat(foundHeadquarter.getItems()).hasSize(2);
//
//        // 각 Item의 headquarter 필드가 올바르게 설정되었는지 확인
//        foundHeadquarter.getItems().forEach(item ->
//                assertThat(item.getHeadquarter()).isEqualTo(foundHeadquarter)
//        );
//
//        // 결과 DTO 검증
//        assertThat(itemResDtos).hasSize(2);
//        List<String> itemNames = itemResDtos.stream()
//                .map(ItemResponse::name)
//                .collect(Collectors.toList());
//        assertThat(itemNames).containsExactlyInAnyOrder("item1", "item2");
//    }
//
//
//    /*
//        pageable 기능을 fake로 구현하는건 너무 투머치인거 같고
//        Mockito를 써서 jpa의 pageable 기능을 모킹할 수는 있지만
//        pageable의 기능이 제대로 동작하는지 확인하고 싶은건데 이걸 stubbing을 하는건 의미가 없는거같은데..
//    */
//    @Test
//    @DisplayName("Headquarter에 연관된 Item들을 조회한다.")
//    void testGetItems_hibernateQuery() {
//        // given
//        User user = createUser();
//        Headquarter headquarter = Headquarter.builder()
//                .franchiseName("test")
//                .restaurantCategory(RestaurantCategory.FASTFOOD)
//                .restaurantSubCategory(RestaurantSubCategory.NONE)
//                .user(user)
//                .build();
//
//        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
//        Long savedHeadquarterId = savedHeadquarter.getId();
//
//
//        Headquarter foundHeadquarter = headquarterRepository.findById(savedHeadquarterId)
//                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
//        List<ItemResponse> savedItemResDtos = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            Item item = Item.builder()
//                    .name("item" + i)
//                    .price(1000)
//                    .headquarter(foundHeadquarter)
//                    .build();
//            Item savedItem = itemRepository.save(item);
//            savedItemResDtos.add(ItemResponse.fromEntity(savedItem));
//        }
//
//        // when
//        Slice<ItemResponse> itemResDtos1 = headquarterService.getItems(user, PageRequest.of(0,5));
//        Slice<ItemResponse> itemResDtos2 = headquarterService.getItems(user, PageRequest.of(1,5));
//
//        // then
//        assertThat(itemResDtos1).hasSize(5);
//        List<String> itemNames = itemResDtos1.stream()
//                .map(ItemResponse::name)
//                .collect(Collectors.toList());
//        assertThat(itemNames).containsExactlyInAnyOrder("item1", "item2", "item3", "item4", "item5");
//
//        assertThat(itemResDtos2).hasSize(5);
//        itemNames = itemResDtos2.stream()
//                .map(ItemResponse::name)
//                .collect(Collectors.toList());
//        assertThat(itemNames).containsExactlyInAnyOrder("item6", "item7", "item8", "item9", "item10");
//    }
//
//}