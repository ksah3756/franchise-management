package com.goorm.friendchise.domain.promotion.application;

import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.Role;
import com.goorm.friendchise.domain.promotion.domain.Promotion;
import com.goorm.friendchise.domain.promotion.domain.PromotionRepository;
import com.goorm.friendchise.domain.notification.event.PromotionCreatedEvent;
import com.goorm.friendchise.domain.promotion.dto.request.PromotionCreateRequest;
import com.goorm.friendchise.domain.promotion.infrastructure.FakePromotionRepository;
import com.goorm.friendchise.global.auth.application.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static com.goorm.friendchise.domain.manager.domain.Role.HEADQUARTER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromotionServiceTest {
	private PromotionService promotionService;
	private PromotionRepository promotionRepository;
	private ApplicationEventPublisher eventPublisher;
	private Manager headquarterManager;
	private Manager storeManager;

	@BeforeEach
	void setUp() {
		promotionRepository = new FakePromotionRepository();
		eventPublisher = mock(ApplicationEventPublisher.class);
		promotionService = new PromotionService(promotionRepository, eventPublisher);

		headquarterManager = Manager.builder()
			.username("testHQ")
			.password("testHQ")
			.role(Role.HEADQUARTER)
			.manageId(1L)
			.build();

		storeManager = Manager.builder()
			.username("testST")
			.password("testST")
			.role(Role.STORE)
			.manageId(2L)
			.build();
	}

	private Manager createManager(Long headquarterId) {
		return Manager.builder()
				.id(1L)
				.username("test")
				.password("test1234")
				.role(HEADQUARTER)
				.manageId(headquarterId)
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
