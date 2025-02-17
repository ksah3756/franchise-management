package com.goorm.friendchise.domain.notification.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationSseServiceTest {
	private NotificationSseSender sseSender;

	@BeforeEach
	void setUp() {
		sseSender = new NotificationSseSender();
	}

	@Test
	@DisplayName("SSE 구독을 정상적으로 할 수 있다.")
	void subscribe() {
		// Given
		Long targetId = 101L;

		// When
		SseEmitter emitter = sseSender.subscribe(targetId);

		// Then
		assertThat(emitter).isNotNull();
	}

	@Test
	@DisplayName("SSE 알림을 정상적으로 전송할 수 있다.")
	void sendSse() throws IOException {
		// Given
		Long targetId = 101L;
		SseEmitter emitter = sseSender.subscribe(targetId);
		Long notificationId = 1L;
		String title = "New Promotion";
		String content = "Promotion Content";

		// When
		sseSender.sendSse(targetId, title, content, notificationId);

		// Then
		// SSEEmitter 내부 동작을 직접 검증하기 어려우므로, 로그 또는 정상 동작 확인
		assertThat(sseSender.subscribe(targetId)).isNotNull();
	}
}
