package com.goorm.friendchise.domain.headquarter.application;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterDetailResDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterReqDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterResDto;
import com.goorm.friendchise.domain.headquarter.dto.store.StoreIdDto;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.store.exception.NoAuthenticationException;
import com.goorm.friendchise.global.auth.managerevent.ManagerUpdateEvent;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HeadquarterService {

    private final HeadquarterRepository headquarterRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public HeadquarterResDto createHeadquarter(Manager currentManager, HeadquarterReqDto headquarterReqDto) {
        checkIfFranchiseNameExists(headquarterReqDto);
        Headquarter headquarter = HeadquarterReqDto.toEntity(headquarterReqDto);
        headquarterRepository.save(headquarter);

        // Manager의 manageId를 업데이트하기 위한 이벤트 발행
        eventPublisher.publishEvent(ManagerUpdateEvent.create(headquarter.getId(), currentManager));
        return HeadquarterResDto.from(headquarter);
    }

    @Transactional(readOnly = true)
    public HeadquarterDetailResDto getHeadquarter(Manager currentManager) {
        Headquarter headquarter = getHeadquarterByContext(currentManager);
        return HeadquarterDetailResDto.from(headquarter);
    }


    @Transactional
    public HeadquarterResDto updateHeadquarterName(Manager currentManager, HeadquarterReqDto headquarterReqDto) {
        Headquarter headquarter = getHeadquarterById(currentManager);

        headquarter.updateByRequestDto(headquarterReqDto);
        return HeadquarterResDto.from(headquarter);
    }

    @Transactional
    public void deleteHeadquarter(Manager currentManager) {
        // TODO: hard delete 대신 soft delete로 구현
        headquarterRepository.deleteById(currentManager.getManageId());
        currentManager.updateManageId(null);
    }

    @Transactional(readOnly = true)
    public List<StoreIdDto> getStoreIdList(Long id) {
        Headquarter headquarter = getHeadquarterById(id);
        return headquarter.getStores().stream()
                .map(store -> StoreIdDto.of(store.getId()))
                .toList();
    }

    public Headquarter getHeadquarterByContext(Manager manager) {
        return getHeadquarterById(manager);
    }

    private void checkIfFranchiseNameExists(HeadquarterReqDto headquarterReqDto) {
        if(headquarterRepository.existsByFranchiseName(headquarterReqDto.franchiseName())) {
            throw new CustomException(ErrorCode.FRANCHISE_NAME_DUPLICATION);
        }
    }

    private Headquarter getHeadquarterById(Manager currentManager) {
        if(currentManager.getManageId() == null) {
            throw new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND);
        }

        return headquarterRepository.findById(currentManager.getManageId())
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }

    private Headquarter getHeadquarterById(Long id) {
        return headquarterRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }

}
