package com.goorm.friendchise.domain.headquarter.Item.application;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import com.goorm.friendchise.domain.headquarter.Item.domain.ItemRepository;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDto;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDtoList;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemResDto;
import com.goorm.friendchise.domain.headquarter.Item.infrastructure.ItemRepositoryImpl;
import com.goorm.friendchise.domain.headquarter.domain.category.Category;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.domain.category.SubCategory;
import com.goorm.friendchise.domain.headquarter.infrastructure.HeadquarterRepositoryImpl;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.infrastructure.FakeManagerRepository;
import com.goorm.friendchise.domain.store.infrastructure.StoreRepository;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.goorm.friendchise.domain.manager.domain.Role.HEADQUARTER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = {"com.goorm.friendchise.domain.headquarter", "com.goorm.friendchise.domain.store"})
@EnableJpaRepositories(basePackages = {"com.goorm.friendchise.domain.headquarter", "com.goorm.friendchise.domain.store"})
@Import({HeadquarterRepositoryImpl.class, ItemRepositoryImpl.class})
class ItemServiceTest {

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ItemRepository itemRepository;

    private ManagerRepository managerRepository;

    private ItemService itemService;


    @BeforeEach
    void setup() {
        // authService에 의존성이 많아서(역할이 많아서)..본시/매장과 고객을 분리하고 토큰 관련 로직도 분리하는게 어떨까..?
        managerRepository = new FakeManagerRepository();

        itemService = new ItemService(headquarterRepository, itemRepository);
    }

    // TODO: AfterEach로 테스트 독립성 보장

    private void setContextHolder(Manager manager) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(manager, manager.getUsername(), manager.getAuthorities())
        );
    }

    private Manager createManager(Long headquarterId) {
        return Manager.builder()
                .id(1L)
                .username("test")
                .password("test1234")
                .role(HEADQUARTER)
                .manageId(headquarterId)
                .build();
    }

    @Test
    @DisplayName("Cascade persist로 다대일 양방향 연관관계인 Headquarter외 Item들이 모두 잘 저장되는지 확인")
    void testCreateItems_CascadePersist() {
        // given: Headquarter 생성 및 저장
        Headquarter headquarter = Headquarter.builder()
                .franchiseName("test")
                .category(Category.FASTFOOD)
                .subCategory(SubCategory.NONE)
                .build();
        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
        Long savedHeadquarterId = savedHeadquarter.getId();
        Manager manager = createManager(savedHeadquarter.getId());

        // given: ItemReqDtoList 생성 (예: 2개의 아이템 요청)
        List<ItemReqDto> itemReqDtos = List.of(
                ItemReqDto.of("item1", 1000),
                ItemReqDto.of("item2", 2000)
        );
        ItemReqDtoList itemReqDtoList = new ItemReqDtoList(itemReqDtos);

        // when: createItems 메서드 호출
        List<ItemResDto> itemResDtos = itemService.createItems(manager, itemReqDtoList);

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
                .map(ItemResDto::name)
                .collect(Collectors.toList());
        assertThat(itemNames).containsExactlyInAnyOrder("item1", "item2");
    }


    /*
        pageable 기능을 fake로 구현하는건 너무 투머치인거 같고
        Mockito를 써서 jpa의 pageable 기능을 모킹할 수는 있지만
        pageable의 기능이 제대로 동작하는지 확인하고 싶은건데 이걸 stubbing을 하는건 의미가 없는거같은데..
    */
    @Test
    @DisplayName("Headquarter에 연관된 Item들을 조회한다.")
    void testGetItems_hibernateQuery() {
        // given
        Headquarter headquarter = Headquarter.builder()
                .franchiseName("test")
                .category(Category.FASTFOOD)
                .subCategory(SubCategory.NONE)
                .build();
        Headquarter savedHeadquarter = headquarterRepository.save(headquarter);
        Long savedHeadquarterId = savedHeadquarter.getId();
        Manager manager = createManager(savedHeadquarter.getId());

        Headquarter foundHeadquarter = headquarterRepository.findById(savedHeadquarterId)
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
        List<ItemResDto> savedItemResDtos = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Item item = Item.builder()
                    .name("item" + i)
                    .price(1000)
                    .headquarter(foundHeadquarter)
                    .build();
            Item savedItem = itemRepository.save(item);
            savedItemResDtos.add(ItemResDto.fromEntity(savedItem));
        }

        // when
        Slice<ItemResDto> itemResDtos1 = itemService.getItems(manager, PageRequest.of(0,5));
        Slice<ItemResDto> itemResDtos2 = itemService.getItems(manager, PageRequest.of(1,5));

        // then
        assertThat(itemResDtos1).hasSize(5);
        List<String> itemNames = itemResDtos1.stream()
                .map(ItemResDto::name)
                .collect(Collectors.toList());
        assertThat(itemNames).containsExactlyInAnyOrder("item1", "item2", "item3", "item4", "item5");

        assertThat(itemResDtos2).hasSize(5);
        itemNames = itemResDtos2.stream()
                .map(ItemResDto::name)
                .collect(Collectors.toList());
        assertThat(itemNames).containsExactlyInAnyOrder("item6", "item7", "item8", "item9", "item10");
    }

}