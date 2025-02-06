package com.goorm.friendchise.domain.notification.presentation;

import com.goorm.friendchise.domain.notification.application.NotificationService;
import com.goorm.friendchise.domain.notification.domain.Notification;
import com.goorm.friendchise.domain.notification.domain.NotificationTarget;
import com.goorm.friendchise.domain.notification.presentation.dto.NotificationCreateRequest;
import com.goorm.friendchise.domain.notification.presentation.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;

	//  알림 생성
	@PostMapping("/create")
	public ResponseEntity<NotificationResponse> createNotification(@RequestBody NotificationCreateRequest request) {
		Notification notification = notificationService.createNotification(request);

		return ResponseEntity.ok(new NotificationResponse(notification.getId(), "success", "알림이 생성되었습니다."));
	}

	//  특정 대상의 알림 목록 조회
	@GetMapping("/{targetType}/{targetId}")
	public ResponseEntity<List<Notification>> getNotifications(@PathVariable NotificationTarget targetType, @PathVariable Long targetId) {
		return ResponseEntity.ok(notificationService.getNotificationsByTarget(targetType, targetId));
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
}
