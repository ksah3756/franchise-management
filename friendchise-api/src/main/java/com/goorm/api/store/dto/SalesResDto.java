package com.goorm.api.store.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SalesResDto(
        Long id,
        LocalDate date,
        String writer
) {
}
