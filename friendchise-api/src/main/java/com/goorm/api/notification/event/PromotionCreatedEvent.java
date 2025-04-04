package com.goorm.api.notification.event;

import com.goorm.core.promotion.domain.Promotion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PromotionCreatedEvent {
    private final Promotion promotion;
}
