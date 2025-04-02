package com.goorm.api.headquarter.dto.item;


import com.goorm.core.headquarter.domain.Item;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ItemRequest(
        @NotBlank(message = "상품 이름은 필수입니다.")
        @Size(min = 1, max = 50, message = "상품 이름은 1자 이상 50자 이하로 입력해주세요.")
        String name,
        @PositiveOrZero(message = "상품 가격은 0 이상의 숫자로 입력해주세요.")
        @Max(Integer.MAX_VALUE)
        int price
) {
        public static ItemRequest of(String name, int price) {
                return new ItemRequest(name, price);
        }
        public static Item toEntity(ItemRequest itemRequest) {
                return Item.of(itemRequest.name(), itemRequest.price());
        }
}
