package com.goorm.api.headquarter.implement.headquarter;


import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.HeadquarterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeadquarterReader {
    private final HeadquarterRepository headquarterRepository;

    public Headquarter getHeadquarterByUserId(Long userId) {
        return headquarterRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }

    public Headquarter getHeadquarterById(Long id) {
        return headquarterRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }

    public Headquarter getHeadquarterByFranchiseName(String franchiseName) {
        return headquarterRepository.findByFranchiseName(franchiseName)
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }
}
