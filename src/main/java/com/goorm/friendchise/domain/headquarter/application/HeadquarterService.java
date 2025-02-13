package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterReqDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterResDto;
import com.goorm.friendchise.domain.headquarter.dto.store.StoreIdDto;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.store.exception.NoAuthenticationException;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HeadquarterService {

    private final AuthService authService;
    private final HeadquarterRepository headquarterRepository;

    private Manager getCurrentManager(){
        return authService.findManagerByAuth();
    }

    @Transactional
    public HeadquarterResDto createHeadquarter(HeadquarterReqDto headquarterReqDto) {
        Manager currentManager = getCurrentManager();

        checkIfFranchiseNameExists(headquarterReqDto);
        Headquarter headquarter = HeadquarterReqDto.toEntity(headquarterReqDto);
        headquarterRepository.save(headquarter);
        currentManager.updateManageId(headquarter.getId());
        return HeadquarterResDto.from(headquarter);
    }

    @Transactional(readOnly = true)
    public HeadquarterResDto getHeadquarter() {
        Manager currentManager = getCurrentManager();
        Headquarter headquarter = findHeadquarterById(currentManager);
        return HeadquarterResDto.from(headquarter);
    }



    @Transactional
    public HeadquarterResDto updateHeadquarterName(HeadquarterReqDto headquarterReqDto) {
        Manager currentManager = getCurrentManager();
        Headquarter headquarter = findHeadquarterById(currentManager);

        findIfMine(headquarter, currentManager);

        headquarter.updateFranchiseName(headquarterReqDto.franchiseName());
        return HeadquarterResDto.from(headquarter);
    }

    @Transactional
    public void deleteHeadquarter() {
        Manager currentManager = getCurrentManager();
        Headquarter headquarter = findHeadquarterById(currentManager);

        findIfMine(headquarter, currentManager);

        headquarterRepository.deleteById(currentManager.getManageId());
        currentManager.updateManageId(null);
    }

    @Transactional(readOnly = true)
    public List<StoreIdDto> getStoreIdList(Long id) {
        Manager currentManager = getCurrentManager();
        Headquarter headquarter = findHeadquarterById(currentManager);
        return headquarter.getStores().stream()
                .map(store -> StoreIdDto.of(store.getId()))
                .toList();
    }

    private void checkIfFranchiseNameExists(HeadquarterReqDto headquarterReqDto) {
        if(headquarterRepository.existsByFranchiseName(headquarterReqDto.franchiseName())) {
            throw new CustomException(ErrorCode.FRANCHISE_NAME_DUPLICATION);
        }
    }

    private Headquarter findHeadquarterById(Manager currentManager) {
        if(currentManager.getManageId() == null) {
            throw new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND);
        }

        return headquarterRepository.findById(currentManager.getManageId())
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }

    private static void findIfMine(Headquarter headquarter, Manager currentManager) {
        if(headquarter == null || !headquarter.getId().equals(currentManager.getManageId())){
            throw new NoAuthenticationException();
        }
    }

}
