package com.goorm.friendchise.domain.notification.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

	@Test
	void create_success() {
		Notification notification = Notification.create(101L, NotificationTarget.STORE, NotificationType.PROMOTION, "test-title", "test-content");

		assertNotNull(notification);
		assertEquals(101L, notification.getTargetId());
		assertEquals(NotificationTarget.STORE, notification.getTargetType());
		assertEquals(NotificationType.PROMOTION, notification.getNotificationType());
		assertEquals("test-title", notification.getTitle());
		assertEquals("test-content", notification.getContent());
	}

	@Test
	void markAsRead_success() {
		Notification notification = Notification.create(101L, NotificationTarget.STORE, NotificationType.PROMOTION, "test-title", "test-content");
		notification.markAsRead();
		Assertions.assertThat(notification.isRead()).isTrue();
	}
}