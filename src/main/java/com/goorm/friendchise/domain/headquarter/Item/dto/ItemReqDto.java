package com.goorm.friendchise.domain.headquarter.Item.dto;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record ItemReqDto(
        @NotBlank @Size(min = 1, max = 50) String name,
        @PositiveOrZero @Max(Integer.MAX_VALUE) int price
) {
        public static ItemReqDto of(String name, int price) {
                return new ItemReqDto(name, price);
        }
        public static Item toEntity(ItemReqDto itemReqDto) {
                return Item.of(itemReqDto.name(), itemReqDto.price());
        }
}
