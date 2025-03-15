package com.goorm.friendchise.domain.headquarter.implement.headquarter;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeadquarterReader {
    private final HeadquarterRepository headquarterRepository;

    public Headquarter getHeadquarterByManager(Manager manager) {
        return getHeadquarterById(manager.getManageId());
    }

    public Headquarter getHeadquarterById(Long id) {
        if(id == null) {
            throw new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND);
        }
        return headquarterRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));
    }
}
