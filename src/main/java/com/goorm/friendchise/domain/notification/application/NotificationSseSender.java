package com.goorm.friendchise.domain.notification.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSseSender {
	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	public void sendSse(Long targetId, String title, String content, Long notificationId) {
		SseEmitter emitter = emitters.get(targetId);
		if (emitter == null) {
			log.info("🚨 SSE Emitter 없음: targetId={}", targetId);
			return;
		}

		try {
			log.info("📢 SSE 전송 시작: targetId={}, title={}", targetId, title);

			// JSON 형태로 데이터 구성
			Map<String, String> payload = new HashMap<>();
			payload.put("notificationId", notificationId.toString());
			payload.put("title", title);
			payload.put("content", content);

			// Gson 또는 Jackson으로 직렬화
			String json = new ObjectMapper().writeValueAsString(payload);

			emitter.send(SseEmitter.event().name("Promotion").data(json));
			log.info("✅ SSE 전송 성공: targetId={}", targetId);
		} catch (IOException e) {
			emitters.remove(targetId);
			log.error("❌ SSE 전송 실패", e);
		}
	}


	public SseEmitter subscribe(Long targetId) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.put(targetId, emitter);

		log.info("해당 매장 구독 완료 {}", targetId);

		try {
			emitter.send(SseEmitter.event().name("dummy").data("SSE 연결됨"));
			log.info("SSE 초기 더미 이벤트 전송 완료: targetId = {}", targetId);
		} catch (IOException e) {
			log.error("SSE 초기 이벤트 전송 실패: {}", e.getMessage());
			emitters.remove(targetId);
		}

		emitter.onCompletion(() -> {
			removeEmitter("SSE 연결 종료", targetId);
		});

		emitter.onTimeout(() -> {
			removeEmitter("SSE 타임 아웃", targetId);
		});

		return emitter;
	}

	private void removeEmitter(String reason, Long targetId) {
		log.info("{}: {}", reason, targetId);
		emitters.remove(targetId);
	}
}
