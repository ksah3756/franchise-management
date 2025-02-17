package com.goorm.friendchise.domain.notification.application;

import com.goorm.friendchise.domain.headquarter.application.HeadquarterService;
import com.goorm.friendchise.domain.headquarter.dto.store.StoreIdDto;
import com.goorm.friendchise.domain.notification.domain.Notification;
import com.goorm.friendchise.domain.notification.event.PromotionCreatedEvent;
import com.goorm.friendchise.domain.promotion.domain.Promotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class NotificationEventHandlerTest {

	@Mock
	private HeadquarterService headquarterService;

	@Mock
	private NotificationManager notificationManager;

	@Mock
	private NotificationSseSender notificationSseSender;

	@InjectMocks
	private NotificationEventHandler eventHandler;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("프로모션 생성 이벤트를 처리하고 알림을 생성 후 SSE를 전송한다.")
	void handlePromotionCreated() {
		// Given
		Long headquarterId = 1L;
		String title = "New Promotion";
		String content = "Promotion Details";

		List<StoreIdDto> storeIds = List.of(new StoreIdDto(10101L), new StoreIdDto(10102L));
		Promotion promotion = Promotion.create(headquarterId, title, content,
			LocalDateTime.of(2025, 3, 1, 9, 0),
			LocalDateTime.of(2025, 3, 7, 23, 59)
		);
		PromotionCreatedEvent event = new PromotionCreatedEvent(promotion);

		List<Notification> mockedNotifications = storeIds.stream()
			.map(storeId -> Notification.create(storeId.id(), title, content))
			.toList();

		when(headquarterService.getStoreIdList(headquarterId)).thenReturn(storeIds);
		when(notificationManager.createNotifications(storeIds, title, content)).thenReturn(mockedNotifications);

		// When
		eventHandler.handlePromotionCreated(event);

		// Then
		verify(notificationManager, times(1)).createNotifications(storeIds, title, content);

		mockedNotifications.forEach(notification -> verify(notificationSseSender, times(1))
			.sendSse(notification.getStoreId(), notification.getTitle(), notification.getContent(), notification.getId()));
	}
}
