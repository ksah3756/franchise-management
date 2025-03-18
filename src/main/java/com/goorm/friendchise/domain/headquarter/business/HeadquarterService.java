package com.goorm.friendchise.domain.headquarter.business;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterDetailResponse;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterRequest;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterResponse;
import com.goorm.friendchise.domain.headquarter.dto.store.StoreIdDto;
import com.goorm.friendchise.domain.headquarter.implement.headquarter.HeadquarterReader;
import com.goorm.friendchise.domain.headquarter.implement.headquarter.HeadquarterValidator;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.global.event.ManagerUpdateEvent;
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
    private final HeadquarterValidator headquarterValidator;
    private final HeadquarterReader headquarterReader;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public HeadquarterResponse createHeadquarter(Manager currentManager, HeadquarterRequest headquarterRequest) {
        headquarterValidator.checkIfFranchiseNameExists(headquarterRequest);
        Headquarter headquarter = HeadquarterRequest.toHeadquarter(headquarterRequest);
        headquarterRepository.save(headquarter);

        // Manager의 manageId를 업데이트하기 위한 이벤트 발행
        eventPublisher.publishEvent(ManagerUpdateEvent.create(headquarter.getId(), currentManager));
        return HeadquarterResponse.from(headquarter);
    }

    @Transactional(readOnly = true)
    public HeadquarterDetailResponse getHeadquarter(Manager currentManager) {
        Headquarter headquarter = headquarterReader.getHeadquarterByManager(currentManager);
        return HeadquarterDetailResponse.from(headquarter);
    }


    @Transactional
    public HeadquarterResponse updateHeadquarterName(Manager currentManager, HeadquarterRequest headquarterRequest) {
        Headquarter headquarter = headquarterReader.getHeadquarterByManager(currentManager);

        headquarter.update(HeadquarterRequest.toHeadquarter(headquarterRequest));
        return HeadquarterResponse.from(headquarter);
    }

    @Transactional
    public void deleteHeadquarter(Manager currentManager) {
        // TODO: hard delete 대신 soft delete로 구현
        deleteHeadquarterByManager(currentManager);
    }

    @Transactional(readOnly = true)
    public List<StoreIdDto> getStoreIdList(Long id) {
        Headquarter headquarter = headquarterReader.getHeadquarterById(id);
        return headquarter.getStores().stream()
                .map(store -> StoreIdDto.of(store.getId()))
                .toList();
    }

    private void deleteHeadquarterByManager(Manager currentManager) {
        if(currentManager.getManageId() == null) {
            throw new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND);
        }
        headquarterRepository.deleteById(currentManager.getManageId());
        currentManager.updateManageId(null);
    }

    @Transactional(readOnly = true)
    public List<StoreIdDto> getStores(Manager currentManager) {
        Headquarter headquarter = headquarterReader.getHeadquarterByManager(currentManager);
        return headquarter.getStores().stream()
                .map(store -> StoreIdDto.of(store.getId()))
                .toList();
    }
}
