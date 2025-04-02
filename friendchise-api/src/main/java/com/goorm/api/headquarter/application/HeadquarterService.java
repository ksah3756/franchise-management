package com.goorm.api.headquarter.application;

import com.goorm.api.headquarter.dto.headquarter.HeadquarterDetailResponse;
import com.goorm.api.headquarter.dto.headquarter.HeadquarterCreateRequest;
import com.goorm.api.headquarter.dto.headquarter.HeadquarterResponse;
import com.goorm.api.headquarter.dto.headquarter.HeadquarterUpdateRequest;
import com.goorm.api.headquarter.dto.item.ItemRequestList;
import com.goorm.api.headquarter.dto.item.ItemResponse;
import com.goorm.api.headquarter.dto.store.StoreIdDto;
import com.goorm.api.headquarter.implement.headquarter.HeadquarterReader;
import com.goorm.api.headquarter.implement.headquarter.HeadquarterValidator;
import com.goorm.api.headquarter.implement.headquarter.HeadquarterWriter;
import com.goorm.api.headquarter.implement.item.ItemReader;
import com.goorm.api.headquarter.implement.item.ItemWriter;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.user.domain.User;


import com.goorm.core.user.domain.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class HeadquarterService {
    private final HeadquarterValidator headquarterValidator;
    private final HeadquarterReader headquarterReader;
    private final HeadquarterWriter headquarterWriter;
    private final ItemReader itemReader;
    private final ItemWriter itemWriter;
    private final EntityManager entityManager;

    @Transactional
    public HeadquarterResponse createHeadquarter(User user, HeadquarterCreateRequest request) {
        headquarterValidator.checkIfFranchiseNameExists(request);
        Headquarter headquarter = HeadquarterCreateRequest.toEntity(user.getId(), request);
        headquarterWriter.createHeadquarter(headquarter);

        return HeadquarterResponse.from(headquarter);
    }

    @Transactional(readOnly = true)
    public HeadquarterDetailResponse getHeadquarter(User user) {
        Headquarter headquarter = headquarterReader.getHeadquarterByUserId(user.getId());
        return HeadquarterDetailResponse.from(headquarter);
    }


    @Transactional
    public HeadquarterResponse updateHeadquarter(User user, HeadquarterUpdateRequest request) {
        Headquarter headquarter = headquarterReader.getHeadquarterByUserId(user.getId());

        headquarterWriter.updateHeadquarter(headquarter, HeadquarterUpdateRequest.toEntity(request));
        return HeadquarterResponse.from(headquarter);
    }

    @Transactional
    public void deleteHeadquarter(User user) {
        // TODO: hard delete 대신 soft delete로 구현
        Headquarter headquarter = headquarterReader.getHeadquarterByUserId(user.getId());
        headquarterWriter.deleteHeadquarter(headquarter.getId());
    }

    @Transactional(readOnly = true)
    public List<StoreIdDto> getStoreIdList(Long id) {
        Headquarter headquarter = headquarterReader.getHeadquarterById(id);
        return headquarter.getStores().stream()
                .map(store -> StoreIdDto.of(store.getId()))
                .toList();
    }


    @Transactional(readOnly = true)
    public List<StoreIdDto> getStores(User user) {
        Headquarter headquarter = headquarterReader.getHeadquarterByUserId(user.getId());
        return headquarter.getStores().stream()
                .map(store -> StoreIdDto.of(store.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Slice<ItemResponse> getItems(User user, Pageable pageable) {
        Headquarter headquarter = headquarterReader.getHeadquarterByUserId(user.getId());
        return itemReader.getItems(headquarter, pageable);
    }

    @Transactional
    public List<ItemResponse> createItems(User user, ItemRequestList itemRequestList) {
        Headquarter headquarter = headquarterReader.getHeadquarterByUserId(user.getId());
        return itemWriter.createItems(headquarter, itemRequestList);
    }
}
