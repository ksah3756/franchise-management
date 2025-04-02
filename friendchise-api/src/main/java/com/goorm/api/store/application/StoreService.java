package com.goorm.api.store.application;

import com.goorm.api.headquarter.implement.headquarter.HeadquarterReader;
import com.goorm.api.headquarter.implement.headquarter.HeadquarterValidator;
import com.goorm.api.store.dto.StoreReqDto;
import com.goorm.api.store.dto.StoreResDto;
import com.goorm.api.store.dto.res.AddressDetailDto;
import com.goorm.api.store.dto.res.KakaoApiRes;
import com.goorm.api.store.dto.res.StoreCreateResponse;
import com.goorm.api.store.exception.NoAuthenticationException;
import com.goorm.api.store.exception.NotFoundAddressException;
import com.goorm.api.store.implement.AddressProvider;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.store.domain.Store;
import com.goorm.core.store.domain.StoreRepository;
import com.goorm.core.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final HeadquarterReader headquarterReader;
    private final AddressProvider addressProvider;
    private final HeadquarterValidator headquarterValidator;

    public List<AddressDetailDto> searchAddress(User user, String address) {
        return addressProvider.getAddressInfos(address);
    }

    @Transactional
    public StoreCreateResponse createStore(User user, StoreReqDto req) {
        Headquarter headquarter = headquarterReader.getHeadquarterByFranchiseName(req.franchiseName());

        // 본사의 certification number와 일치하는 경우에만 스토어 생성 가능
        headquarterValidator.validateCertificationNumber(headquarter.getId(), req.certificationNumber());

        Store store = Store.create(
                req.name(),
                req.address(),
                req.dong(),
                req.x(),
                req.y(),
                req.franchiseName(),
                headquarter.getId(),
                user
        );
        storeRepository.save(store);

        // saveStoreToRedis(store); // 이것도 할거면 트랜잭션 밖에서 해야지

        log.info("초기 스토어 생성 완료 storeId = {}", store.getId());
        return StoreCreateResponse.fromEntity(store);
	}

    @Transactional(readOnly = true)
    public StoreResDto getStoreInfo(User user) {
        Store store = findIfStoreExists(user);

        return StoreResDto.fromEntity(store);
    }

    @Transactional
    public void updateStoreInfo(User user, StoreReqDto req) {
        Store store = findIfStoreExists(user);
        Headquarter headquarter = headquarterReader.getHeadquarterByFranchiseName(req.franchiseName());

        Store newStore = Store.create(
                req.name(),
                req.address(),
                req.dong(),
                req.x(),
                req.y(),
                req.franchiseName(),
                headquarter.getId(),
                user
        );
        store.updateStore(newStore);
    }

    @Transactional
    public void deleteStore(User user){
        Store store = findIfStoreExists(user);

//        String storeKey = "store:" + store.getId();
//        redisTemplate.delete(storeKey);

        storeRepository.deleteById(store.getId());
    }

    private Store findIfStoreExists(User user) {
        return storeRepository.findById(user.getId()).orElseThrow(NoAuthenticationException::new);
    }

}
