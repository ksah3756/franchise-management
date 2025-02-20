package com.goorm.friendchise.domain.notification.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

class NotificationSseServiceTest {
	private NotificationSseSender sseSender;
	private NotificationManager notificationManagerMock;
	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		notificationManagerMock = Mockito.mock(NotificationManager.class);
		doNothing().when(notificationManagerMock).verifyRole(anyString());
		sseSender = new NotificationSseSender(objectMapper, notificationManagerMock);
	}

	@Test
	@DisplayName("SSE 구독을 정상적으로 할 수 있다.")
	void subscribe() {
		// Given
		Long targetId = 101L;
		String dummyToken = "dummyToken"; // 검증을 위해 사용할 더미 토큰

		// When
		SseEmitter emitter = sseSender.subscribe(targetId, dummyToken);

		// Then
		assertThat(emitter).isNotNull();
	}

	@Test
	@DisplayName("SSE 알림을 정상적으로 전송할 수 있다.")
	void sendSse() throws IOException {
		// Given
		Long targetId = 101L;
		String dummyToken = "dummyToken";
		SseEmitter emitter = sseSender.subscribe(targetId, dummyToken);
		Long notificationId = 1L;
		String title = "New Promotion";
		String content = "Promotion Content";

		// When
		sseSender.sendSse(targetId, title, content, notificationId);

		// Then
		// 구독 상태가 유지되는지 확인 (실제 발송 결과는 로그로 확인)
		SseEmitter emitter2 = sseSender.subscribe(targetId, dummyToken);
		assertThat(emitter2).isNotNull();
	}
}
