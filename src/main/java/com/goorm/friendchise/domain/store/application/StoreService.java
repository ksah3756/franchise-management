package com.goorm.friendchise.domain.store.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.notification.application.NotificationSseSender;
import com.goorm.friendchise.domain.store.domain.Store;
import com.goorm.friendchise.domain.store.dto.StoreRedisDto;
import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import com.goorm.friendchise.domain.store.dto.StoreResDto;
import com.goorm.friendchise.domain.store.dto.res.KakaoApiAddressResDto;
import com.goorm.friendchise.domain.store.dto.res.KakaoApiRes;
import com.goorm.friendchise.domain.store.exception.NoAuthenticationException;
import com.goorm.friendchise.domain.store.exception.NotFoundAddressException;
import com.goorm.friendchise.domain.store.exception.StoreNotFoundException;
import com.goorm.friendchise.domain.store.infrastructure.StoreRepository;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor

public class StoreService {

    @Value("${kakao.api.findPosition}")
    private String findPosition;

    private final StoreRepository storeRepository;
    private final HeadquarterRepository headquarterRepository;
    private final WebClient webClient;
    private final AuthService authService;
    private final NotificationSseSender notificationSseSender;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper; // JSON 변환용
	private Manager getCurrentManager(){
        return authService.findManagerByAuth();
    }

    public List<KakaoApiAddressResDto> searchAddress(String address) {

        KakaoApiRes query = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(findPosition)
                        .queryParam("query", address)
                        .build())
                .retrieve()
                .bodyToMono(KakaoApiRes.class)
                .block();

        if(query == null || query.getDocuments().isEmpty()) {
            throw new NotFoundAddressException();
        }

        return getCollect(query);
    }

    @Transactional
    public void createStore(StoreReqDto req) {
        Manager currentManager = getCurrentManager();
        Headquarter headquarter = findHeadquarterByHeadQuarterName(req.headQuarterName());

        Store store = new Store(req, headquarter, currentManager);
        storeRepository.save(store);

        currentManager.updateManageId(store.getId());

        saveStoreToRedis(store);

        log.info("초기 스토어 생성 완료 storeId = {}", store.getId());
	}

    @Transactional(readOnly = true)
    public StoreResDto getStoreInfo() {
        Manager currentManager = getCurrentManager();
        Store store = findIfStoreExists(currentManager);

        return new StoreResDto(store);
    }

    @Transactional
    public void updateStoreInfo(StoreReqDto req) {
        Manager currentManager = getCurrentManager();
        Store store = findIfStoreExists(currentManager);
        Headquarter headquarter = findHeadquarterByHeadQuarterName(req.headQuarterName());

        findIfMine(store, currentManager);

        store.updateStore(req, headquarter);
    }

    @Transactional
    public void deleteStore(){
        Manager currentManager = getCurrentManager();
        Store store = findIfStoreExists(currentManager);

        findIfMine(store, currentManager);

        String storeKey = "store:" + store.getId();
        redisTemplate.delete(storeKey);

        currentManager.updateManageId(null);
        storeRepository.delete(store);
    }

    /*--------------------------------------------------------------------------------*/

    private static List<KakaoApiAddressResDto> getCollect(KakaoApiRes query) {
        return query.getDocuments().stream()
                .filter(doc -> doc.getRoad_address() != null)
                .map(doc -> {
                    String address = doc.getRoad_address().getAddress_name();
                    String roadAddress = doc.getRoad_address().getAddress_name();
                    String zoneNumber = doc.getRoad_address().getZone_no();
                    Double x = Double.valueOf(doc.getRoad_address().getX());
                    Double y = Double.valueOf(doc.getRoad_address().getY());
                    String dong = doc.getRoad_address().getRegion_3depth_name();
                    return new KakaoApiAddressResDto(address, roadAddress, zoneNumber, dong, x, y);
                })
                .collect(Collectors.toList());
    }

    private static void findIfMine(Store store, Manager currentManager) {
        if(store == null || !store.getId().equals(currentManager.getManageId())) {
            throw new NoAuthenticationException();
        }
    }

    private Store findIfStoreExists(Manager currentManager) {
        if(currentManager.getManageId() == null)
            throw new StoreNotFoundException();

        return storeRepository.findById(currentManager.getManageId()).orElseThrow(NoAuthenticationException::new);
    }

    private Headquarter findHeadquarterByHeadQuarterName(String franchiseName) {
        return headquarterRepository.findByFranchiseName(franchiseName)
                .orElseThrow(() -> new CustomException(ErrorCode.FRANCHISE_NOT_FOUND));
    }

    public List<StoreRedisDto> fetchAndCacheStoresFromDB() {
        List<Store> stores = storeRepository.findAll();
        List<StoreRedisDto> storeRedis= new ArrayList<>();
        for(Store store : stores)
            storeRedis.add(saveStoreToRedis(store));
        return storeRedis;
    }

    private StoreRedisDto saveStoreToRedis(Store store) {
        String storeKey = "store:" + store.getId();
        StoreRedisDto storeDto = StoreRedisDto.builder()
                .pointX(store.getPointX())
                .pointY(store.getPointY())
                .manageId(store.getManageId())
                .franchiseName(store.getFranchiseName())
                .headquarterId(store.getHeadquarter().getId())
                .address(store.getAddress())
                .dong(store.getDong())
                .build();

        try {
            String storeJson = objectMapper.writeValueAsString(storeDto);
            redisTemplate.opsForValue().set(storeKey, storeJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 저장 중 JSON 변환 오류", e);
        }
        return storeDto;
    }

}
