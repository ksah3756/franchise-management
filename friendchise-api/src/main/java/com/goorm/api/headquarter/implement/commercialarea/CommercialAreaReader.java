package com.goorm.api.headquarter.implement.commercialarea;

import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.friendchise.domain.headquarter.infrastructure.CommercialAreaRepository;
import com.goorm.friendchise.domain.headquarter.domain.CommercialArea;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommercialAreaReader {
    private final CommercialAreaRepository commercialAreaRepository;

    public CommercialArea getCommercialArea(double x, double y) {
        String point = String.format("POINT(%f %f)", y, x);
        List<CommercialArea> area = commercialAreaRepository.findByPoint(point);
        if(area.isEmpty()) {
            throw new CustomException(ErrorCode.REGION_NOT_SUPPORTED);
        }
        return area.get(0);
    }
}
