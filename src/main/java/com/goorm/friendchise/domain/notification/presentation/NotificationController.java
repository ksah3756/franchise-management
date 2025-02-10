package com.goorm.friendchise.domain.notification.presentation;

import com.goorm.friendchise.domain.notification.application.NotificationService;
import com.goorm.friendchise.domain.notification.dto.response.NotificationResponse;
import com.goorm.friendchise.domain.notification.dto.response.ReceivedNotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;

	// 특정 targetId에 대한 알림 목록 조회 API
	@GetMapping("/{targetId}")
	public ResponseEntity<List<ReceivedNotificationResponse>> getNotifications(@PathVariable("targetId") Long targetId) {
		List<ReceivedNotificationResponse> notifications = notificationService.getNotificationsByTarget(targetId);
		return ResponseEntity.ok(notifications);
	}

	//  알림 읽음 처리
	@PatchMapping("/{notificationId}/read")
	public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long notificationId) {
		notificationService.markAsRead(notificationId);
		return ResponseEntity.ok(new NotificationResponse(notificationId, "success", "알림이 읽음 처리되었습니다."));
	}

	//  알림 삭제
	@DeleteMapping("/{notificationId}")
	public ResponseEntity<NotificationResponse> deleteNotification(@PathVariable Long notificationId) {
		notificationService.deleteNotification(notificationId);
		return ResponseEntity.ok(new NotificationResponse(notificationId, "success", "알림이 삭제되었습니다."));
	}

	// SSE 구독 (매장 실시간 알림 받기)
	@GetMapping("/subscribe/{targetId}")
	public SseEmitter subscribe(@PathVariable("targetId") Long targetId) {
		return notificationService.subscribe(targetId);
	}
}
