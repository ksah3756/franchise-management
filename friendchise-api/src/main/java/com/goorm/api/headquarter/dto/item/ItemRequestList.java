package com.goorm.api.headquarter.dto.item;

import jakarta.validation.Valid;

import java.util.List;

public record ItemRequestList(
        @Valid List<ItemRequest> itemList
) {
}
