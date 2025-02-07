package com.goorm.friendchise.domain.headquarter.Item.dto;

import jakarta.validation.Valid;

import java.util.List;

public record ItemReqDtoList(
        @Valid List<ItemReqDto> itemList
) {
}
