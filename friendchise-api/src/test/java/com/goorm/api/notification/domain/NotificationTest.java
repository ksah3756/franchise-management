package com.goorm.api.notification.domain;

import com.goorm.core.notification.domain.Notification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

	@Test
	void create_success() {
		// given
		Long id = 1L;
		Long targetId = 001L;
		String title = "title";
		String content = "content";
		boolean isRead = false;

		// when
		Notification notification = new Notification(id, targetId, title, content, isRead);

		// then
		assertThat(notification.getId()).isEqualTo(1L);
		assertThat(notification.getId()).isEqualTo(001L);
		assertThat(notification.getTitle()).isEqualTo("title");
		assertThat(notification.getContent()).isEqualTo("content");
		assertThat(notification.isRead()).isFalse();
	}

	@Test
	void isRead_success() {
		// given
		Notification notification = Notification.create(001L, "title", "content");

		// when
		notification.markAsRead();

		// then
		assertThat(notification.isRead()).isEqualTo(true);
	}
}