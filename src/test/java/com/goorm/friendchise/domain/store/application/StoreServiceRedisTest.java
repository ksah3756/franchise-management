package com.goorm.friendchise.domain.store.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorm.friendchise.domain.customer.infrastructure.FakeStoreRepository;
import com.goorm.friendchise.domain.headquarter.application.HeadquarterService;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterReqDto;
import com.goorm.friendchise.domain.headquarter.insfrastructure.FakeHeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.domain.manager.infrastructure.FakeManagerRepository;
import com.goorm.friendchise.domain.redis.config.RedisConfigTest;
import com.goorm.friendchise.domain.store.dto.StoreRedisDto;
import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.infrastructure.FakeRefreshTokenRepository;
import com.goorm.friendchise.global.auth.jwt.JwtProperties;
import com.goorm.friendchise.global.auth.jwt.TokenProvider;
import com.goorm.friendchise.global.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;

import static com.goorm.friendchise.domain.manager.domain.Role.HEADQUARTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataRedisTest
@Import({RedisConfigTest.class,TokenProvider.class, JwtProperties.class})
public class StoreServiceRedisTest {
    private StoreService storeService;
    @Autowired
    private RedisTemplate<String, Object > redisTemplate;

    @Autowired
    private TokenProvider tokenProvider;

    private Manager manager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private  RedisService redisService;
    @BeforeEach
    void setUp() {
        FakeStoreRepository fakeStoreRepository = new FakeStoreRepository();
        ManagerRepository managerRepository = new FakeManagerRepository();
        FakeHeadquarterRepository headquarterRepository = new FakeHeadquarterRepository();

        FakeRefreshTokenRepository refreshTokenRepository = new FakeRefreshTokenRepository();
        AuthService authService = new AuthService(managerRepository, tokenProvider,
                refreshTokenRepository, null,null,fakeStoreRepository);

        redisService = new RedisService(redisTemplate);

        storeService= new StoreService(fakeStoreRepository,headquarterRepository,
                null,authService,null,
                redisTemplate,objectMapper);
        manager = Manager.create("test", "test1234", HEADQUARTER);
        managerRepository.save(
                manager
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(manager, manager.getUsername(), manager.getAuthorities())
        );

        HeadquarterReqDto headquarterReqDto = HeadquarterReqDto.of("test", "패스트푸드", "");
        HeadquarterService headquarterService = new HeadquarterService(authService, headquarterRepository);
        headquarterService.createHeadquarter(headquarterReqDto);
    }

    @Test
    void 생성시_레디스에_저장되었는_지_확인() throws JsonProcessingException {
        StoreReqDto reqDto = StoreReqDto.builder()
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드 중곡점")
                .headQuarterName("test")
                .build();

        storeService.createStore(reqDto);

        String storeKey = "store:11";
        Object value = redisTemplate.opsForValue().get(storeKey);
        System.out.println(value);
        String jsonValue = value.toString();
        StoreRedisDto storeDto = objectMapper.readValue(jsonValue, StoreRedisDto.class);


        storeService.deleteStore();

        Object value2 = redisTemplate.opsForValue().get(storeKey);

        System.out.println(value2);

    }

    @Test
    void 레디스에_있는_모든_store를_불러오기() throws JsonProcessingException {
        StoreReqDto reqDto = StoreReqDto.builder()
                .address("광진구 중곡동")
                .roadAddress("광진구 중곡동 천호대로116 9길")
                .zoneNumber("04930")
                .dong("중곡동")
                .x(30.14578)
                .y(18.345724)
                .franchiseName("맥도날드 중곡점")
                .headQuarterName("test")
                .build();

        storeService.createStore(reqDto);
        storeService.createStore(reqDto);
        storeService.createStore(reqDto);
        storeService.createStore(reqDto);

        List<StoreRedisDto> stores= redisService.getAllStoresFromRedis();

        StoreRedisDto storeDto = redisService.getStoreFromRedisById(11L);
        System.out.println("id로 가져온 값:"+storeDto);

        for(StoreRedisDto store : stores)
        {
            System.out.println(store);
        }

        //레디스 비우기
        for(int i=0;i<4;i++){
            String storeKey = "store:" + (11+i);
            redisTemplate.delete(storeKey);
        }

    }
}
