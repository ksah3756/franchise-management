package com.goorm.core.headquarter.domain;

import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantCategoryTest {

    @Test
    @DisplayName("RestaurantCategory의 값과 일치하지 않는 문자열을 입력하면 예외를 반환한다.")
    void fromString() {
        CustomException ex = assertThrows(CustomException.class, () -> RestaurantCategory.fromString("베트남음식"));
        assertEquals(ErrorCode.FRANCHISE_CATEGORY_NOT_FOUND, ex.getErrorCode());
    }
}