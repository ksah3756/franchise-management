package com.goorm.friendchise.domain.notification.application;

import com.goorm.friendchise.domain.notification.domain.*;
import com.goorm.friendchise.domain.notification.dto.response.ReceivedNotificationResponse;
import com.goorm.friendchise.domain.notification.event.PromotionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final NotificationRepository repository;
	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	@EventListener
	public void handlePromotionCreated(PromotionCreatedEvent promotion) {
		log.info("프로모션 생성 이벤트 감지 완료: {}", promotion);

		Long headquarterId = promotion.getPromotion().getHeadquarterId();
		String title = promotion.getPromotion().getTitle();
		String content = promotion.getPromotion().getContent();

		List<Long> storeIds = findStoresByHeadquarter(headquarterId);

		List<Notification> notifications = storeIds.stream()
			.map(storeId -> Notification.create(storeId, title, content))
			.toList();

		saveAllNotification(notifications);

		storeIds.forEach(storeId -> sendSse(storeId, title, content));

	}

	@Transactional
	public void saveAllNotification(List<Notification> notifications) {
		repository.saveAll(notifications);
		log.info("알림 {}개 저장 완료", notifications.size());
	}

	private void sendSse(Long targetId, String title, String content) {
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

		// 더미 이벤트 전송 (연결 유지 확인용)
		try {
			emitter.send(SseEmitter.event().name("dummy").data("SSE 연결됨"));
			log.info("SSE 초기 더미 이벤트 전송 완료: targetId = {}", targetId);
		} catch (IOException e) {
			log.error("SSE 초기 이벤트 전송 실패: {}", e.getMessage());
			emitters.remove(targetId);
		}

		emitter.onCompletion(() -> {
			log.info("SSE 연결 종료: targetId = {}", targetId);
			emitters.remove(targetId);
		});

		emitter.onTimeout(() -> {
			log.info("SSE 타임아웃 발생: targetId = {}", targetId);
			emitters.remove(targetId);
		});

		return emitter;
	}

	@Transactional
	public void markAsRead(Long notificationId) {
		Notification notification = repository.findById(notificationId)
			.orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
		notification.markAsRead();
	}

	@Transactional
	public void deleteNotification(Long notificationId) {
		repository.delete(notificationId);
	}

	// 본사에 속한 매장 ID 목록 조회 (예제용, 실제 DB 조회 필요)
	private List<Long> findStoresByHeadquarter(Long headquarterId) {
		return List.of(10101L, 10102L);
	}

	public List<ReceivedNotificationResponse> getNotificationsByTarget(Long targetId) {
		return repository.findByTargetId(targetId).stream()
			.map(notification -> ReceivedNotificationResponse.builder()
				.title(notification.getTitle())
				.content(notification.getContent())
				.build())
			.collect(Collectors.toList());
	}
}
