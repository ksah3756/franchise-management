package com.goorm.friendchise.domain.headquarter.Item.dto;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record ItemReqDto(
        @NotBlank(message = "상품 이름은 필수입니다.")
        @Size(min = 1, max = 50, message = "상품 이름은 1자 이상 50자 이하로 입력해주세요.")
        String name,
        @PositiveOrZero(message = "상품 가격은 0 이상의 숫자로 입력해주세요.")
        @Max(Integer.MAX_VALUE)
        int price
) {
        public static ItemReqDto of(String name, int price) {
                return new ItemReqDto(name, price);
        }
        public static Item toEntity(ItemReqDto itemReqDto) {
                return Item.of(itemReqDto.name(), itemReqDto.price());
        }
}
