package com.goorm.friendchise.domain.headquarter.implement.headquarter;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeadquarterCreator {
    private final HeadquarterRepository headquarterRepository;

    public Headquarter createHeadquarter(HeadquarterRequest headquarterRequest) {
        Headquarter headquarter = HeadquarterRequest.toHeadquarter(headquarterRequest);
        return headquarterRepository.save(headquarter);
    }
}
