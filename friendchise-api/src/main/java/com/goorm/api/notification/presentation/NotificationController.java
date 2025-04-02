package com.goorm.api.notification.presentation;

import com.goorm.api.auth.resolver.AuthUser;
import com.goorm.api.notification.application.NotificationManager;
import com.goorm.api.notification.application.NotificationSseSender;
import com.goorm.api.notification.dto.response.NotificationResponse;
import com.goorm.api.notification.dto.response.ReceivedNotificationResponse;
import com.goorm.core.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationManager notificationManager;
	private final NotificationSseSender notificationSseSender;

	@Secured("ROLE_STORE")
	@GetMapping("/my")
	public ResponseEntity<List<ReceivedNotificationResponse>> getNotifications(
			@AuthUser User user
	) {
		List<ReceivedNotificationResponse> notifications = notificationManager.getNotifications(user);
		return ResponseEntity.ok(notifications);
	}

	@Secured("ROLE_STORE")
	@PatchMapping("/{notificationId}/read")
	public ResponseEntity<NotificationResponse> markAsRead(
			@PathVariable("notificationId") Long notificationId,
			@AuthUser User user
	) {
		notificationManager.markAsRead(user, notificationId);
		return ResponseEntity.ok(new NotificationResponse(notificationId, "success", "알림이 읽음 처리되었습니다."));
	}

	@Secured("ROLE_STORE")
	@DeleteMapping("/{notificationId}")
	public ResponseEntity<NotificationResponse> deleteNotification(
			@PathVariable("notificationId") Long notificationId,
			@AuthUser User user
	) {
		notificationManager.deleteNotification(user, notificationId);
		return ResponseEntity.ok(new NotificationResponse(notificationId, "success", "알림이 삭제되었습니다."));
	}

	@GetMapping("/subscribe/{storeId}")
	public SseEmitter subscribe(@PathVariable("storeId") Long storeId,
								@RequestParam("token") String token,
								@AuthUser User user) {
		return notificationSseSender.subscribe(user, storeId,token);
	}
}
