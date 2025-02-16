package com.goorm.friendchise.domain.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSseSender {
	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	public void sendSse(Long targetId, String title, String content) {
		SseEmitter emitter = emitters.get(targetId);
		if (emitter == null) return;

		try {
			log.info("SSE 전송 시작: targetId={}, title={}", targetId, title);
			emitter.send(SseEmitter.event().name("Promotion").data(content));
		} catch (IOException e) {
			emitters.remove(targetId);
			log.error("SSE 전송 실패", e);
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
