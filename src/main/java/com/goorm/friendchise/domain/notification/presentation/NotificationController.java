package com.goorm.friendchise.domain.notification.presentation;

import com.goorm.friendchise.domain.notification.application.NotificationManager;
import com.goorm.friendchise.domain.notification.application.NotificationSseSender;
import com.goorm.friendchise.domain.notification.dto.response.NotificationResponse;
import com.goorm.friendchise.domain.notification.dto.response.ReceivedNotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Secured("ROLE_STORE")
public class NotificationController {
	private final NotificationManager notificationManager;
	private final NotificationSseSender notificationSseSender;

	// 스토어 - 해당 스토어에 발생한 알림 조회
	@GetMapping("/my")
	public ResponseEntity<List<ReceivedNotificationResponse>> getNotifications() {
		List<ReceivedNotificationResponse> notifications = notificationManager.getNotifications();
		return ResponseEntity.ok(notifications);
	}

	//  알림 읽음 처리
	@PatchMapping("/{notificationId}/read")
	public ResponseEntity<NotificationResponse> markAsRead(@PathVariable("notificationId") Long notificationId) {
		notificationManager.markAsRead(notificationId);
		return ResponseEntity.ok(new NotificationResponse(notificationId, "success", "알림이 읽음 처리되었습니다."));
	}

	//  알림 삭제
	@DeleteMapping("/{notificationId}")
	public ResponseEntity<NotificationResponse> deleteNotification(@PathVariable("notificationId") Long notificationId) {
		notificationManager.deleteNotification(notificationId);
		return ResponseEntity.ok(new NotificationResponse(notificationId, "success", "알림이 삭제되었습니다."));
	}

	// SSE 구독 - 스토어 (추후 유저 기능 추가)
	@GetMapping("/subscribe/{storeId}")
	public SseEmitter subscribe(@PathVariable("storeId") Long storeId) {
		return notificationSseSender.subscribe(storeId);
	}
}
