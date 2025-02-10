package com.goorm.friendchise.domain.notification.application;

import com.goorm.friendchise.domain.notification.domain.Notification;
import com.goorm.friendchise.domain.notification.domain.NotificationRepository;
import com.goorm.friendchise.domain.notification.event.PromotionCreatedEvent;
import com.goorm.friendchise.domain.notification.infrastructure.FakeNotificationRepository;
import com.goorm.friendchise.domain.promotion.domain.Promotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTest {
	private NotificationService notificationService;
	private NotificationRepository notificationRepository;

	@BeforeEach
	void setUp() {
		notificationRepository = new FakeNotificationRepository();
		notificationService = new NotificationService(notificationRepository);
	}

	@Test
	void handlePromotionCreated_success() {
		// given
		PromotionCreatedEvent event = new PromotionCreatedEvent(
			Promotion.create(1L,
				"할인 이벤트",
				"전 매장 50% 할인",
				LocalDateTime.of(2025, 3, 1, 9, 0),
				LocalDateTime.of(2025, 3, 7, 23, 59))
		);

		// when
		notificationService.handlePromotionCreated(event);

		// then
		List<Notification> notifications = notificationRepository.findAll();
		assertThat(notifications).hasSize(2);  // 기본적으로 3개 매장에 전송
		assertThat(notifications.get(0).getTitle()).isEqualTo("할인 이벤트");
	}

	@Test
	void markAsRead_success() {
		// given
		Notification notification = notificationRepository.save(Notification.create(101L, "긴급 점검", "서버 점검"));
		Long notificationId = notification.getId();

		// when
		notificationService.markAsRead(notificationId);

		// then
		Optional<Notification> updatedNotification = notificationRepository.findById(notificationId);
		assertThat(updatedNotification).isPresent();
		assertThat(updatedNotification.get().isRead()).isTrue();
	}

	@Test
	void deleteNotification_success() {
		// given
		Notification notification = notificationRepository.save(Notification.create(101L, "삭제 테스트", "이 알림은 삭제됩니다."));
		Long notificationId = notification.getId();

		// when
		notificationService.deleteNotification(notificationId);

		// then
		Optional<Notification> deletedNotification = notificationRepository.findById(notificationId);
		assertThat(deletedNotification).isEmpty();
	}

	@Test
	void subscribe_success() {
		// given
		Long storeId = 101L;

		// when
		SseEmitter emitter = notificationService.subscribe(storeId);

		// then
		assertThat(emitter).isNotNull();
	}
}
