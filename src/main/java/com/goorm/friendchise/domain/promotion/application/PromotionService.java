package com.goorm.friendchise.domain.promotion.application;

import com.goorm.friendchise.domain.promotion.domain.Promotion;
import com.goorm.friendchise.domain.promotion.domain.PromotionRepository;
import com.goorm.friendchise.domain.notification.event.PromotionCreatedEvent;
import com.goorm.friendchise.domain.promotion.dto.request.PromotionCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PromotionService {
	private final PromotionRepository promotionRepository;
	private final ApplicationEventPublisher eventPublisher;

	public void createPromotion(PromotionCreateRequest request) {
		Promotion promotion = Promotion.create(request.headquarterId(), request.title(), request.content(), request.startDate(), request.endDate());
		promotionRepository.save(promotion);

		log.info("프로모션 저장 완료: {}", promotion.getTitle());
		eventPublisher.publishEvent(new PromotionCreatedEvent(promotion));
		log.info("프로모션 이벤트 발행 완료: {}", promotion.getTitle());
	}
}
