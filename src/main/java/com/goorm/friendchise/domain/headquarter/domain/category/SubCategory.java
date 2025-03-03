package com.goorm.friendchise.domain.headquarter.domain.category;

import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum SubCategory {
    NONE(""),
    // 분식
    TTEOKBOKKI("떡볶이"),
    // 한식
    MEAT("고기"),
    NOODLE("국수"),
    GOOKBAB("국밥"),
    SEAFOOD("해물"),
    // 중식
    // 일식
    TONKATSU("돈까스"),
    SUSHI("초밥"),
    UDON("우동"),
    RAMEN("라멘"),

    // 양식
    PIZZA("피자"),
    STEAK("스테이크"),
    ITALIAN("이탈리안"),
    HAMBURGER("햄버거"),
    MEXICAN("멕시칸"),
    // 디저트
    // 간식
    // 술집
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

