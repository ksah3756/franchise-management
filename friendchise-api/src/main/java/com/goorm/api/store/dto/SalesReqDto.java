package com.goorm.api.store.dto;

public record SalesReqDto(
        String today,
        String dailySales,
        String writer
) {

}
