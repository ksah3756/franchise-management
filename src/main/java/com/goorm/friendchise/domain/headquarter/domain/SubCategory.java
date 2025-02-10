package com.goorm.friendchise.domain.headquarter.domain;

import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum SubCategory {
    NONE(""),
    ;
    private final String value;

    SubCategory(String value) {
        this.value = value;
    }

    public static SubCategory fromString(String value) {
        for (SubCategory subCategory : SubCategory.values()) {
            if (subCategory.getValue().equalsIgnoreCase(value)) {
                return subCategory;
            }
        }
        throw new CustomException(ErrorCode.FRANCHISE_SUBCATEGORY_NOT_FOUND);
    }
}

