package com.goorm.friendchise.domain.notification.application;

import com.goorm.friendchise.domain.headquarter.dto.store.StoreIdDto;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.Role;
import com.goorm.friendchise.domain.notification.domain.Notification;
import com.goorm.friendchise.domain.notification.dto.response.ReceivedNotificationResponse;
import com.goorm.friendchise.domain.notification.infrastructure.FakeNotificationRepository;
import com.goorm.friendchise.global.auth.application.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationManagerTest {
	private NotificationManager notificationManager;
	private FakeNotificationRepository repository;
	private AuthService authService;

	@BeforeEach
	void setUp() {
		repository = new FakeNotificationRepository();
		authService = Mockito.mock(AuthService.class);
		notificationManager = new NotificationManager(repository, authService);
	}

	@Test
	@DisplayName("알림을 생성하고 저장할 수 있다.")
	void createNotifications() {
		// Given
		List<StoreIdDto> storeIds = List.of(new StoreIdDto(101L), new StoreIdDto(102L));
		String title = "New Promotion";
		String content = "Promotion Details";

		// When
		List<Notification> notifications = notificationManager.createNotifications(storeIds, title, content);

		// Then
		assertThat(notifications).hasSize(2);
		assertThat(repository.findAll()).hasSize(2);
	}

	@Test
	@DisplayName("로그인한 스토어 인증을 통해 알림을 조회할 수 있다.")
	void getNotifications() {
		// Given
		Long storeId = 101L;
		Manager mockManager = Manager.builder()
			.manageId(storeId)
			.role(Role.STORE)
			.build();

		when(authService.findManagerByAuth()).thenReturn(mockManager);

		repository.save(Notification.create(101L, "Title1", "Content1"));
		repository.save(Notification.create(101L, "Title2", "Content2"));
		repository.save(Notification.create(102L, "Title3", "Content3")); // 다른 스토어 ID

		// When
		List<ReceivedNotificationResponse> foundNotifications = notificationManager.getNotifications();

		// Then
		assertThat(foundNotifications).hasSize(2);
		assertThat(foundNotifications).extracting("title").contains("Title1", "Title2");
		verify(authService, times(1)).findManagerByAuth();
	}

	@Test
	@DisplayName("알림을 읽음 처리할 수 있다.")
	void markAsRead() {
		// Given
		Long storeId = 101L;
		Notification notification = repository.save(Notification.create(storeId, "Title", "Content"));
		Long notificationId = notification.getId();

		Manager mockManager = Manager.builder()
			.manageId(storeId)
			.role(Role.STORE)
			.build();

		when(authService.findManagerByAuth()).thenReturn(mockManager);

		// When
		notificationManager.markAsRead(notificationId);

		// Then
		Notification updatedNotification = repository.findById(notificationId).orElseThrow();
		assertThat(updatedNotification.isRead()).isTrue();
	}

	@Test
	@DisplayName("알림을 삭제할 수 있다.")
	void deleteNotification() {
		// Given
		Notification notification = repository.save(Notification.create(101L, "Title", "Content"));
		Long notificationId = notification.getId();

		// When
		notificationManager.deleteNotification(notificationId);

		// Then
		assertThat(repository.findById(notificationId)).isEmpty();
	}
}
