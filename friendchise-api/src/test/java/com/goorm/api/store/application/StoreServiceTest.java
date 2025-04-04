package com.goorm.api.store.application;

import com.goorm.api.config.TestWebClientConfig;
import com.goorm.api.store.dto.StoreReqDto;
import com.goorm.api.store.dto.StoreResDto;
import com.goorm.api.store.dto.res.StoreCreateResponse;
import com.goorm.api.store.implement.AddressProvider;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.HeadquarterRepository;
import com.goorm.core.headquarter.domain.RestaurantCategory;
import com.goorm.core.headquarter.domain.RestaurantSubCategory;
import com.goorm.core.store.domain.Store;
import com.goorm.core.store.domain.StoreRepository;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRepository;
import com.goorm.core.user.domain.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Import(TestWebClientConfig.class)
class StoreServiceTest {

    @MockitoBean
    private AddressProvider addressProvider;

    @Autowired
    private HeadquarterRepository headquarterRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreService storeService;

    private User headquarterManager;
    private User storeManager;
    private Headquarter headquarter;

    @BeforeEach
    void setUp() {
        User user = User.create("headquarter", "headquarter1234", UserRole.HEADQUARTER);
        headquarterManager = userRepository.save(user);

       user = User.create("test", "test1234", UserRole.STORE);
       storeManager = userRepository.save(user);

       Headquarter hq = Headquarter.builder()
               .userId(headquarterManager.getId())
               .franchiseName("맥도날드")
               .certificationNumber("123456")
               .restaurantCategory(RestaurantCategory.FASTFOOD)
               .restaurantSubCategory(RestaurantSubCategory.NONE)
               .build();

       headquarter = headquarterRepository.save(hq);
    }

    @AfterEach
    void tearDown() {
        headquarterRepository.deleteAll();
        userRepository.deleteAll();
    }

    @DisplayName("인증된 사용자가 Store을 생성합니다.")
    @Test
    void createStore() {
     // given
        headquarterRepository.save(headquarter);
        StoreReqDto reqDto = StoreReqDto.builder()
                .name("맥도날드 중곡점")
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드")
                .certificationNumber("123456")
                .build();

     // when
        StoreCreateResponse res = storeService.createStore(storeManager, reqDto);

        // then
        assertEquals(res.franchiseName(), reqDto.franchiseName());
    }

    @DisplayName("인증 번호가 일치하지 않을 시 Store을 생성하지 못합니다.")
    @Test
    void createStore_certificationNumber_notMatched() {
        // given
        headquarterRepository.save(headquarter);
        StoreReqDto reqDto = StoreReqDto.builder()
                .name("맥도날드 중곡점")
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드")
                .certificationNumber("1234")
                .build();

        // when, then
        CustomException ex = assertThrows(CustomException.class, () -> storeService.createStore(storeManager, reqDto));
        assertEquals(ex.getErrorCode(), ErrorCode.HEADQUARTER_AUTH_NOT_MATCH);
    }

    @DisplayName("특정 Store를 조회합니다.")
    @Test
    void getStoreInfo(){

     //given
        StoreReqDto req = StoreReqDto.builder()
                .name("맥도날드 중곡점")
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드")
                .certificationNumber("123456")
                .build();

        Store store = Store.create(
                storeManager.getId(),
                req.name(),
                req.address(),
                req.dong(),
                req.x(),
                req.y(),
                req.franchiseName(),
                headquarter.getId()
        );

        storeRepository.save(store);

     //when
        StoreResDto storeInfo = storeService.getStoreInfo(storeManager);

        //then
        assertThat(storeInfo).isNotNull();
        assertThat(storeInfo.address()).isEqualTo(store.getAddress());
        assertThat(storeInfo.franchiseName()).isEqualTo(store.getFranchiseName());
    }

    @DisplayName("Store의 정보를 수정합니다.")
    @Test
    void updateStore(){
     //given
        StoreReqDto reqDto = StoreReqDto.builder()
                .name("맥도날드 중곡점")
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드")
                .certificationNumber("123456")
                .build();

        Store store = Store.create(
                storeManager.getId(),
                reqDto.name(),
                reqDto.address(),
                reqDto.dong(),
                reqDto.x(),
                reqDto.y(),
                reqDto.franchiseName(),
                headquarter.getId()
        );

        storeRepository.save(store);

        StoreReqDto updatedDto = StoreReqDto.builder()
                .name("맥도날드 역삼점")
                .address("강남구 역삼동")
                .roadAddress("서울시 강남구 역삼동")
                .zoneNumber("12345")
                .dong("역삼동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드")
                .certificationNumber("123456")
                .build();

     //when
        storeService.updateStoreInfo(storeManager, updatedDto);
        Store updatedStore = storeRepository.findById(store.getId()).orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        //then
        assertThat(updatedStore.getAddress()).isEqualTo(updatedDto.address());
        assertThat(updatedStore.getName()).isEqualTo(updatedDto.name());
    }

    @DisplayName("Store을 삭제합니다.")
    @Test
    void deleteStore(){
     //given
        StoreReqDto reqDto = StoreReqDto.builder()
                .name("맥도날드 중곡점")
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드 중곡점")
                .certificationNumber("123456")
                .build();

        Store store = Store.create(
                storeManager.getId(),
                reqDto.name(),
                reqDto.address(),
                reqDto.dong(),
                reqDto.x(),
                reqDto.y(),
                reqDto.franchiseName(),
                headquarter.getId()
        );
        Store savedStore = storeRepository.save(store);

        //when
        storeService.deleteStore(storeManager);

        //then
        Optional<Store> deletedStore = storeRepository.findById(savedStore.getId());
        assertThat(deletedStore).isEmpty();
    }

}