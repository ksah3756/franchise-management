package com.goorm.api.promotion.application;

import com.goorm.api.notification.event.PromotionCreatedEvent;
import com.goorm.api.promotion.dto.request.PromotionCreateRequest;
import com.goorm.api.promotion.dto.response.PromotionDetailResponse;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.promotion.domain.Promotion;
import com.goorm.core.promotion.domain.PromotionRepository;
import com.goorm.core.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PromotionService {
	private final PromotionRepository promotionRepository;
	private final ApplicationEventPublisher eventPublisher;

	public void createPromotion(User user, PromotionCreateRequest request) {
		Promotion promotion = Promotion.create(user, request.title(), request.content(), request.startDate(), request.endDate());
		promotionRepository.save(promotion);
		log.info("프로모션 저장 완료: {}", promotion.getTitle());

		eventPublisher.publishEvent(new PromotionCreatedEvent(promotion));
		log.info("프로모션 이벤트 발행 완료: {}", promotion.getTitle());
	}

	@Transactional(readOnly = true)
	public List<PromotionDetailResponse> getMyHeadquarterPromotions(User user) {
		Long headquarterId = Optional.ofNullable(user.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.HEADQUARTER_NOT_FOUND));

		return promotionRepository.findByHeadquarterId(headquarterId)
			.stream()
			.map(this::mapToDetailResponse)
			.collect(Collectors.toList());
	}

	private PromotionDetailResponse mapToDetailResponse(Promotion promotion) {
		return PromotionDetailResponse.builder()
			.id(promotion.getId())
			.title(promotion.getTitle())
			.content(promotion.getContent())
			.startDate(promotion.getStartDate())
			.endDate(promotion.getEndDate())
			.build();
	}
}
