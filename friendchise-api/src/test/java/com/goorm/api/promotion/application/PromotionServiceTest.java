package com.goorm.api.promotion.application;

import com.goorm.api.notification.event.PromotionCreatedEvent;
import com.goorm.api.promotion.dto.request.PromotionCreateRequest;
import com.goorm.api.promotion.infrastructure.FakePromotionRepository;
import com.goorm.core.promotion.domain.Promotion;
import com.goorm.core.promotion.domain.PromotionRepository;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRole;
import org.apache.catalina.Manager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PromotionServiceTest {
	private PromotionService promotionService;
	private PromotionRepository promotionRepository;
	private ApplicationEventPublisher eventPublisher;
	private User headquarterManager;
	private User storeManager;

	@BeforeEach
	void setUp() {
		promotionRepository = new FakePromotionRepository();
		eventPublisher = mock(ApplicationEventPublisher.class);
		promotionService = new PromotionService(promotionRepository, eventPublisher);

		headquarterManager = User.builder()
			.id(1L)
			.username("testHQ")
			.password("testHQ")
			.userRole(UserRole.HEADQUARTER)
			.build();

		storeManager = User.builder()
			.username("testST")
			.password("testST")
			.userRole(UserRole.STORE)
			.build();
	}

	@Test
	void 본사_관리자가_프로모션_생성_성공() {

		PromotionCreateRequest request = new PromotionCreateRequest(
			"깜짝 봄맞이 할인 이벤트", "전 제품 30% 할인!",
			LocalDateTime.of(2025, 3, 1, 9, 0),
			LocalDateTime.of(2025, 3, 7, 23, 59)
		);

		// When
		promotionService.createPromotion(headquarterManager,request);

		// Then
		List<Promotion> promotions = promotionRepository.findAll();
		assertThat(promotions).hasSize(1);
		assertThat(promotions.get(0).getTitle()).isEqualTo("깜짝 봄맞이 할인 이벤트");

		// 이벤트가 정상적으로 발행되었는지 검증
		ArgumentCaptor<PromotionCreatedEvent> eventCaptor = ArgumentCaptor.forClass(PromotionCreatedEvent.class);
		verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

		PromotionCreatedEvent event = eventCaptor.getValue();
		assertThat(event.getPromotion().getTitle()).isEqualTo("깜짝 봄맞이 할인 이벤트");
	}

	@Test
	void 본사_권한이_없는_사용자는_프로모션_생성_실패() {
		// Given (매장 관리자)

		PromotionCreateRequest request = new PromotionCreateRequest(
			"비정상 할인 이벤트", "허위 이벤트",
			LocalDateTime.of(2025, 3, 1, 9, 0),
			LocalDateTime.of(2025, 3, 7, 23, 59)
		);

		// When & Then (예외 발생 확인)
		assertThatThrownBy(() -> promotionService.createPromotion(storeManager, request))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("본사가 아니므로 권한이 없습니다.");
	}
}
