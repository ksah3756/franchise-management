package com.goorm.friendchise.domain.customer.application;

import com.goorm.friendchise.domain.customer.domain.Customer;
import com.goorm.friendchise.domain.customer.domain.CustomerRepository;
import com.goorm.friendchise.domain.customer.dto.request.*;
import com.goorm.friendchise.domain.customer.dto.response.CustomerDetailResponse;
import com.goorm.friendchise.domain.customer.dto.response.CustomerPersistResponse;
import com.goorm.friendchise.domain.customer.exception.CustomerException;
import com.goorm.friendchise.domain.location.application.LocationService;
import com.goorm.friendchise.domain.store.application.StoreService;
import com.goorm.friendchise.domain.store.dto.StoreRedisDto;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.dto.response.TokenResponse;
import com.goorm.friendchise.global.auth.util.DistanceCalculator;
import com.goorm.friendchise.global.exception.ErrorCode;
import com.goorm.friendchise.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final KaKaoApiService kakaoApiService;
    private final RedisTemplate<String, String> redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    private static final int API_TIMEOUT = 3; // OpenAPI 호출 제한 시간 (초)
    private static final int MAX_RETRY = 5; // 최대 재시도 횟수
    private static final long CACHE_EXPIRATION = 10; // 캐싱 지속 시간 (분)
    private static final String CACHE_PREFIX = "nearestStore:";
    private final AuthService authService;
    private final LocationService locationService;
    private final RedisService redisService;
    private final StoreService storeService;
    // 🕒 서비스 실행 시작 시간
    private Instant serviceStartTime = Instant.now();

    @Transactional
    public CustomerPersistResponse create(CustomerCreateRequest request)
    {
        if(customerRepository.existsByUsername(request.username()))
        {
            throw new CustomerException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        Customer customer = customerRepository.save(
                Customer.builder().username(request.username()).password(bCryptPasswordEncoder.encode(request.password())).build());
        return CustomerPersistResponse.of(customer);
    }
    
    @Transactional
    public TokenResponse login(CustomerLoginRequest request
                               ) {
        Customer customer =findCustomerByUsername(request.username());
        customer.isPasswordMatch(request.password(), bCryptPasswordEncoder);

        locationService.saveStartLocation(request.startY(),request.startX(),customer);

        return authService.customerLogin(customer);
    }

    @Transactional
    public void logout(CustomerDestinationRequest request)
    {
        locationService.saveDestinationLocation(request);
    }

    public CustomerDetailResponse myPage()
    {
        return CustomerDetailResponse.from( authService.findCustomerByAuth());
    }

    @Transactional
    public void updatePassword(String newPassword)
    {
        Customer customer=authService.findCustomerByAuth();
        if (newPassword == null || newPassword.isBlank()||newPassword.contains(" ")||newPassword.length()>15||newPassword.length()<8) {
            throw new CustomerException(ErrorCode.INVALID_PASSWORD);
        }
        customer.updatePassword(bCryptPasswordEncoder.encode(newPassword));
    }

    private Customer findCustomerByUsername(String username)
    {
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerException(ErrorCode.USER_NOT_FOUND));
    }
    public String findNearestStoreWithCache(CustomerRecommendStoreRequest customerRecommendStoreRequest)
    {
        String address = customerRecommendStoreRequest.address();

        String cacheKey = CACHE_PREFIX + address;
        String cachedAddress = redisTemplate.opsForValue().get(cacheKey);
        if(cachedAddress != null)
            return logCacheHit("1차",cachedAddress);

        int attempt;
        for (attempt = 0; attempt < MAX_RETRY; attempt++) {
            cachedAddress = redisTemplate.opsForValue().get(cacheKey);
            if(cachedAddress != null)
                return logCacheHit("2차",cachedAddress);

            String nearestAddress = findNearestStore(address,customerRecommendStoreRequest.franchiseName());
            if (nearestAddress != null) {
                if(nearestAddress.contains("cached"))
                    return nearestAddress.split(" ")[0];
                double elapsedTime = Duration.between(serviceStartTime, Instant.now()).toMillis(); // 실행 시간(ms)
                log.info("✅ OPENAPI로 찾은 매장: " + nearestAddress +" 응답시간: " +elapsedTime/1000+"초");
                return nearestAddress;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("스레드 대기 중 인터럽트 발생", e);
            }
        }
        if(attempt==MAX_RETRY)
            throw new CustomerException(ErrorCode.RECOMMEND_API_TIMEOUT);
        else
            throw new CustomerException(ErrorCode.UNKNOWN_ERROR);
    }
    public String findNearestStoreWithNoCache(CustomerRecommendStoreRequest customerRecommendStoreRequest) {
        String address = customerRecommendStoreRequest.address();
        String franchiseName = customerRecommendStoreRequest.franchiseName();


        String nearestAddress = calculateNearestStore(address, franchiseName);

        double elapsedTime = Duration.between(serviceStartTime, Instant.now()).toMillis(); // 실행 시간(ms)
        log.info("✅ OPENAPI로 찾은 매장: " + nearestAddress + " 응답시간: " + elapsedTime / 1000 + "초");
        return nearestAddress;
    }

    private String findNearestStore(String address,String franchiseName)
    {
        String cacheKey = CACHE_PREFIX + address;
        Object lock = locks.computeIfAbsent(address, key -> new Object());
        synchronized (lock){
            try{
                String cachedAddress = redisTemplate.opsForValue().get(cacheKey);
                if(cachedAddress != null)
                    return logCacheHit("3차",cachedAddress);
                String nearestStoreAddress=calculateNearestStore(address,franchiseName);
                redisTemplate.opsForValue().set(cacheKey, nearestStoreAddress, CACHE_EXPIRATION, TimeUnit.MINUTES);
                return nearestStoreAddress;
            } finally {
                locks.remove(address);
            }

        }
    }

    private String calculateNearestStore(String address, String franchiseName) {
        return CompletableFuture
                .supplyAsync(() -> kakaoApiService.getCoordinatesByAddress(address))
                .orTimeout(API_TIMEOUT, TimeUnit.SECONDS)
                .thenApply(customerCoordinates -> {
                    List<StoreRedisDto> stores = redisService.getAllStoresFromRedis();

                    if (stores.isEmpty()) {
                        stores = storeService.fetchAndCacheStoresFromDB();
                    }
                    return stores.stream()
                            .filter(store -> store.franchiseName().equals(franchiseName))
                            .min(Comparator.comparing(store -> DistanceCalculator.calculateDistance(
                                    customerCoordinates.getPointY(), customerCoordinates.getPointX(),
                                    store.pointY(), store.pointX()
                            )))
                            .orElseThrow(() -> new CustomerException(ErrorCode.NEAR_STORE_NOT_FOUND));
                })
                .join().address();
    }

    private String logCacheHit(String cacheLevel, String cachedAddress) {
        double elapsedTime = Duration.between(serviceStartTime, Instant.now()).toMillis(); // 실행 시간(ms)
        log.info("✅ {} 캐싱 확인됨! 캐시된 매장: {} 응답시간: {}초", cacheLevel, cachedAddress, elapsedTime / 1000);
        if(cacheLevel.equals("3차"))
            cachedAddress+=" cached";
        return cachedAddress;
    }

}
