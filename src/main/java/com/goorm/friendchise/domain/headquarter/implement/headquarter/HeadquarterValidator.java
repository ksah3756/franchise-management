package com.goorm.friendchise.domain.headquarter.implement.headquarter;

import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterRequest;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeadquarterValidator {
    private final HeadquarterRepository headquarterRepository;

    public void checkIfFranchiseNameExists(HeadquarterRequest headquarterRequest) {
        if(headquarterRepository.existsByFranchiseName(headquarterRequest.franchiseName())) {
            throw new CustomException(ErrorCode.FRANCHISE_NAME_DUPLICATION);
        }
    }
}
