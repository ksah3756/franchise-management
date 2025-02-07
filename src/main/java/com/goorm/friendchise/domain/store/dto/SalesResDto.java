package com.goorm.friendchise.domain.store.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SalesResDto(
        Long id,
        LocalDate date,
        String writer
) {
}
