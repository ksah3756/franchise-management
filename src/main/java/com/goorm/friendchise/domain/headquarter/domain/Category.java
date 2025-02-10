package com.goorm.friendchise.domain.headquarter.domain;

import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Category {
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

    Category(String value) {
        this.value = value;
    }

    public static Category fromString(String value) {
        for (Category category : Category.values()) {
            if (category.getValue().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new CustomException(ErrorCode.FRANCHISE_CATEGORY_NOT_FOUND);
    }
}
