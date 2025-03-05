package com.goorm.friendchise.domain.store.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorm.friendchise.domain.headquarter.domain.category.Category;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.domain.category.SubCategory;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.store.domain.Store;
import com.goorm.friendchise.domain.store.dto.StoreRedisDto;
import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import com.goorm.friendchise.domain.store.dto.StoreResDto;
import com.goorm.friendchise.domain.store.infrastructure.StoreRepository;
import com.goorm.friendchise.global.auth.application.AuthService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

import static com.goorm.friendchise.domain.manager.domain.Role.HEADQUARTER;
import static com.goorm.friendchise.domain.manager.domain.Role.STORE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    private static Headquarter headquarter;
    private static Manager headManager;
    private static Manager storeManager;

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private HeadquarterRepository headquarterRepository;
    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private WebClient webClient;
    @Mock
    private AuthService authService;
    @InjectMocks
    private StoreService storeService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ValueOperations<String, Object> mockValueOperations;

    @BeforeAll
    static void setUp() {
        headquarter = Headquarter.of("HeadQuarter", Category.FASTFOOD, SubCategory.MEAT);
        ReflectionTestUtils.setField(headquarter, "id", 1L);

        headManager = Manager.create("headQuarter", "test1234", HEADQUARTER);
        ReflectionTestUtils.setField(headManager, "id", 1L);
        headManager.updateManageId(headquarter.getId());

        storeManager = Manager.create("storeManager", "test1234", STORE);
        ReflectionTestUtils.setField(storeManager, "id", 2L);
    }

    @DisplayName("인증된 사용자가 Store을 생성합니다.")
    @Test
    void createStore() throws JsonProcessingException {
     //given
        StoreReqDto reqDto = StoreReqDto.builder()
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드 중곡점")
                .headQuarterName("HeadQuarter")
                .build();

     //when
        when(headquarterRepository.findByFranchiseName(headquarter.getFranchiseName()))
                .thenReturn(Optional.ofNullable(headquarter));
//        when(redisTemplate.opsForValue()).thenReturn(mockValueOperations);
//        when(objectMapper.writeValueAsString(any(StoreRedisDto.class))).thenReturn("jsonString");  // mock 처리

        storeService.createStore(storeManager ,reqDto);
     //then
        verify(headquarterRepository, times(1)).findByFranchiseName(headquarter.getFranchiseName());
        verify(storeRepository).save(any(Store.class));
    }

    @DisplayName("특정 Store를 조회합니다.")
    @Test
    void getStoreInfo(){
     //given
        StoreReqDto reqDto = StoreReqDto.builder()
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드 중곡점")
                .headQuarterName("HeadQuarter")
                .build();
        Store store = new Store(reqDto, headquarter, storeManager);
        ReflectionTestUtils.setField(store, "id", 1L);
        storeManager.updateManageId(store.getId());

     //when
        when(storeRepository.findById(storeManager.getManageId())).thenReturn(Optional.of(store));
        StoreResDto result = storeService.getStoreInfo(storeManager);

     //then
        assertThat(result).isNotNull();
        assertThat(result.address()).isEqualTo(store.getAddress());
        assertThat(result.franchiseName()).isEqualTo(store.getFranchiseName());
    }

    @DisplayName("Store의 정보를 수정합니다.")
    @Test
    void updateStore(){
     //given
        StoreReqDto reqDto = StoreReqDto.builder()
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드 중곡점")
                .headQuarterName("HeadQuarter")
                .build();
        Store store = new Store(reqDto, headquarter, storeManager);
        ReflectionTestUtils.setField(store, "id", 1L);
        storeManager.updateManageId(store.getId());

        StoreReqDto updatedDto = StoreReqDto.builder()
                .address("강남구 역삼동")
                .roadAddress("서울시 강남구 역삼동")
                .zoneNumber("12345")
                .dong("역삼동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드 역삼점")
                .headQuarterName("HeadQuarter")
                .build();

     //when
        when(storeRepository.findById(storeManager.getManageId())).thenReturn(Optional.of(store));
        when(headquarterRepository.findByFranchiseName(headquarter.getFranchiseName())).thenReturn(Optional.ofNullable(headquarter));
        storeService.updateStoreInfo(storeManager,updatedDto);

     //then
        assertThat(store.getAddress()).isEqualTo(updatedDto.address());
        assertThat(store.getFranchiseName()).isEqualTo("맥도날드 역삼점");

        verify(storeRepository, times(1)).findById(storeManager.getManageId());
        verify(headquarterRepository, times(1)).findByFranchiseName("HeadQuarter");
    }

    @DisplayName("Store을 삭제합니다.")
    @Test
    void deleteStore(){
     //given
        StoreReqDto reqDto = StoreReqDto.builder()
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드 중곡점")
                .headQuarterName("HeadQuarter")
                .build();
        Store store = new Store(reqDto, headquarter, storeManager);
        ReflectionTestUtils.setField(store, "id", 1L);
        storeManager.updateManageId(store.getId());

     //when
        when(storeRepository.findById(storeManager.getManageId())).thenReturn(Optional.of(store));

//        when(redisTemplate.delete("store:" + store.getId())).thenReturn(true);

        storeService.deleteStore(storeManager);

        //then
        assertThat(storeManager.getManageId()).isNull();
        verify(storeRepository, times(1)).findById(1L);
    }

}