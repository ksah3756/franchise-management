package com.goorm.friendchise.domain.headquarter.appilcation;

import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDtoList;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemResDto;
import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.dto.HeadquarterReqDto;
import com.goorm.friendchise.domain.headquarter.dto.HeadquarterResDto;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import jakarta.validation.Valid;
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
    public HeadquarterResDto updateHeadquarter(Long id, HeadquarterReqDto headquarterReqDto) {
        Headquarter headquarter = findHeadquarterById(id);
        headquarter.updateFranchiseName(headquarterReqDto.franchiseName());
        return HeadquarterResDto.from(headquarter);
    }

    @Transactional
    public void deleteHeadquarter(Long id) {
        headquarterRepository.deleteById(id);
    }
}
