package com.goorm.friendchise.domain.headquarter.Item.dto;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;

public record ItemResDto(
        Long id,
        String name,
        int price
) {
    public static ItemResDto of(Long id, String name, int price) {
        return new ItemResDto(id, name, price);
    }

    public static ItemResDto fromEntity(Item item) {
        return new ItemResDto(item.getId(), item.getName(), item.getPrice());
    }
}
