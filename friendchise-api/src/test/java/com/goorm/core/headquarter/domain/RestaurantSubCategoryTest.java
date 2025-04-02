package com.goorm.core.headquarter.domain;

import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantSubCategoryTest {

    @Test
    @DisplayName("RestaurantSubCategory의 값과 일치하지 않는 문자열을 입력하면 예외를 반환한다.")
    void fromString() {
        CustomException ex = assertThrows(CustomException.class, () -> RestaurantSubCategory.fromString("치킨"));
        assertEquals(ErrorCode.FRANCHISE_SUBCATEGORY_NOT_FOUND, ex.getErrorCode());
    }
}