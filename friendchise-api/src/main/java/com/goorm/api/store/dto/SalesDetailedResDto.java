package com.goorm.api.store.dto;


import com.goorm.core.store.domain.Sales;

import java.time.LocalDate;

public record SalesDetailedResDto(
        Long id,
        LocalDate date,
        Long dailySales,
        String writer
) {
    public SalesDetailedResDto(Sales sales) {
        this(
                sales.getId(),
                sales.getDate(),
                sales.getDailySales(),
                sales.getWriter()
        );
    }
}
