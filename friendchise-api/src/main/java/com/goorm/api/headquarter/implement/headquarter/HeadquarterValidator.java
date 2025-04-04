package com.goorm.api.headquarter.implement.headquarter;

import com.goorm.api.headquarter.dto.headquarter.HeadquarterCreateRequest;
import com.goorm.core.common.exception.CustomException;

import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.HeadquarterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.goorm.core.common.exception.ErrorCode.HEADQUARTER_NOT_FOUND;
import static com.goorm.core.common.exception.ErrorCode.INVALID_PARAMETER;

@Service
@RequiredArgsConstructor
public class HeadquarterValidator {
    private final HeadquarterRepository headquarterRepository;

    public void checkIfFranchiseNameExists(HeadquarterCreateRequest headquarterCreateRequest) {
        if(headquarterRepository.existsByFranchiseName(headquarterCreateRequest.franchiseName())) {
            throw new CustomException(ErrorCode.FRANCHISE_NAME_DUPLICATION);
        }
    }

    public void validateCertificationNumber(Long id, String certificationNumber) {
        if (id == null)
            throw new CustomException(INVALID_PARAMETER);

        Headquarter hq = headquarterRepository.findById(id)
                .orElseThrow(() -> new CustomException(HEADQUARTER_NOT_FOUND));

        hq.validateCertificationNumber(certificationNumber);
    }
}
