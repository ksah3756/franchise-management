package com.goorm.friendchise.domain.notification.application;

import com.goorm.friendchise.domain.notification.domain.Notification;
import com.goorm.friendchise.domain.notification.domain.NotificationRepository;
import com.goorm.friendchise.domain.notification.domain.NotificationTarget;
import com.goorm.friendchise.domain.notification.domain.NotificationType;
import com.goorm.friendchise.domain.notification.infrastructure.FakeNotificationRepository;
import com.goorm.friendchise.domain.notification.presentation.dto.NotificationCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

		// 기본 알림 미리 생성
		notificationService.createNotification(createTestRequest(101L, "test-title1", "test-content1"));
		notificationService.createNotification(createTestRequest(101L, "test-title2", "test-content2"));
	}

	@Test
	void createNotification_success() {
		// given
		NotificationCreateRequest request = createTestRequest(102L, "new-title", "new-content");

		// when
		Notification notification = notificationService.createNotification(request);

		// then
		assertThat(notification).isNotNull();
		assertThat(notification.getTargetId()).isEqualTo(102L);
		assertThat(notification.getTitle()).isEqualTo("new-title");
		assertThat(notification.getContent()).isEqualTo("new-content");

		// 추가 검증: Fake Repository에 저장되었는지 확인
		List<Notification> notifications = notificationRepository.findByTargetTypeAndTargetId(NotificationTarget.STORE, 102L);
		assertThat(notifications).hasSize(1);
	}

	@Test
	void getNotificationsByTarget_success() {
		// when
		List<Notification> notifications = notificationService.getNotificationsByTarget(NotificationTarget.STORE, 101L);

		// then
		assertThat(notifications).hasSize(2);
		assertThat(notifications.get(0).getTitle()).isEqualTo("test-title1");
		assertThat(notifications.get(1).getTitle()).isEqualTo("test-title2");
	}

	@Test
	void markAsRead_success() {
		// given
		Notification notification = notificationService.createNotification(createTestRequest(103L, "mark-read", "content"));

		// when
		notificationService.markAsRead(notification.getId());

		// then
		Optional<Notification> updatedNotification = notificationRepository.findById(notification.getId());
		assertThat(updatedNotification).isPresent();
		assertThat(updatedNotification.get().isRead()).isTrue();
	}

	@Test
	void deleteNotification_success() {
		// given
		Notification notification = notificationService.createNotification(createTestRequest(104L, "delete-test", "content"));

		// when
		notificationService.deleteNotification(notification.getId());

		// then
		Optional<Notification> deletedNotification = notificationRepository.findById(notification.getId());
		assertThat(deletedNotification).isEmpty();
	}

	// 중복 제거: 테스트용 알림 생성 메서드 추가
	private NotificationCreateRequest createTestRequest(Long targetId, String title, String content) {
		return NotificationCreateRequest.builder()
			.targetId(targetId)
			.targetType(NotificationTarget.STORE)
			.notificationType(NotificationType.PROMOTION)
			.title(title)
			.content(content)
			.build();
	}
}
