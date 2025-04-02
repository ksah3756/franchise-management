package com.goorm.core.headquarter.domain;

import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum RestaurantCategory {
    FASTFOOD("패스트푸드"),
    SNACKFOOD("분식"),
    KOREANFOOD("한식"),
    CHINESEFOOD("중식"),
    JAPANESEFOOD("일식"),
    WESTERNFOOD("양식"),
    CAFE("카페"),
    DESSERT("디저트"),
    SNACK("간식"),
    BAR("술집")
    ;

    private final String value;

    RestaurantCategory(String value) {
        this.value = value;
    }

    public static RestaurantCategory fromString(String value) {
        for (RestaurantCategory restaurantCategory : RestaurantCategory.values()) {
            if (restaurantCategory.getValue().equalsIgnoreCase(value)) {
                return restaurantCategory;
            }
        }
        throw new CustomException(ErrorCode.FRANCHISE_CATEGORY_NOT_FOUND);
    }
}
