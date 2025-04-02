package com.goorm.api.headquarter.dto.item;


import com.goorm.core.headquarter.domain.Item;

public record ItemResponse(
        Long id,
        String name,
        int price
) {
    public static ItemResponse of(Long id, String name, int price) {
        return new ItemResponse(id, name, price);
    }

    public static ItemResponse fromEntity(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getPrice());
    }
}
