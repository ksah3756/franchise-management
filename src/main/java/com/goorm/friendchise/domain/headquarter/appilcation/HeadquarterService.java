package com.goorm.friendchise.domain.headquarter.appilcation;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterReqDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterResDto;
import com.goorm.friendchise.domain.headquarter.dto.store.StoreIdDto;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HeadquarterService {
    private final HeadquarterRepository headquarterRepository;

    @Transactional
    public HeadquarterResDto createHeadquarter(HeadquarterReqDto headquarterReqDto) {
        checkIfFranchiseNameExists(headquarterReqDto);
        Headquarter headquarter = HeadquarterReqDto.toEntity(headquarterReqDto);
        headquarterRepository.save(headquarter);
        return HeadquarterResDto.from(headquarter);
    }

    private void checkIfFranchiseNameExists(HeadquarterReqDto headquarterReqDto) {
        if(headquarterRepository.existsByFranchiseName(headquarterReqDto.franchiseName())) {
            throw new CustomException(ErrorCode.FRANCHISE_NAME_DUPLICATION);
        }
    }

    @Transactional(readOnly = true)
    public HeadquarterResDto getHeadquarter(Long id) {
        Headquarter headquarter = findHeadquarterById(id);
        return HeadquarterResDto.from(headquarter);
    }

    private Headquarter findHeadquarterById(Long id) {
        return headquarterRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }

    @Transactional
    public HeadquarterResDto updateHeadquarterName(Long id, HeadquarterReqDto headquarterReqDto) {
        Headquarter headquarter = findHeadquarterById(id);
        headquarter.updateFranchiseName(headquarterReqDto.franchiseName());
        return HeadquarterResDto.from(headquarter);
    }

    @Transactional
    public void deleteHeadquarter(Long id) {
        headquarterRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<StoreIdDto> getStoreIdList(Long id) {
        Headquarter headquarter = findHeadquarterById(id);
        return headquarter.getStores().stream()
                .map(store -> StoreIdDto.of(store.getId()))
                .toList();
    }

}
