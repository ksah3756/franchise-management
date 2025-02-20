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
	private final ObjectMapper objectMapper;
	private final NotificationManager notificationManager;

	public void sendSse(Long targetId, String title, String content, Long notificationId) {
		SseEmitter emitter = emitters.get(targetId);
		if (emitter == null) {
			log.warn("SSE Emitter not found for targetId: {}", targetId);
			return;
		}

		try {
			Map<String, String> payload = new HashMap<>();
			payload.put("notificationId", notificationId.toString());
			payload.put("title", title);
			payload.put("content", content);

			String json = objectMapper.writeValueAsString(payload);
			emitter.send(SseEmitter.event().name("Promotion").data(json));
			log.info("SSE sent for targetId: {}", targetId);
		} catch (IOException e) {
			emitters.remove(targetId);
			emitter.completeWithError(e);
			log.error("SSE send failed for targetId: {}", targetId, e);
		}
	}

	public SseEmitter subscribe(Long targetId, String token) {
		notificationManager.verifyRole(token);

		SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
		emitters.put(targetId, emitter);
		log.info("Subscription successful for targetId: {}", targetId);

		try {
			emitter.send(SseEmitter.event().name("dummy").data("SSE connected"));
		} catch (IOException e) {
			emitters.remove(targetId);
			emitter.completeWithError(e);
			log.error("Initial SSE send failed for targetId: {}", targetId, e);
		}

		emitter.onCompletion(() -> emitters.remove(targetId));
		emitter.onTimeout(() -> {
			emitters.remove(targetId);
			emitter.complete();
			log.info("SSE timeout for targetId: {}", targetId);
		});
		emitter.onError(ex -> {
			emitters.remove(targetId);
			log.error("SSE error for targetId: {}", targetId, ex);
		});

		return emitter;
	}
}
