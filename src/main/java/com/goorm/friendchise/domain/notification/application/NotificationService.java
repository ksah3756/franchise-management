package com.goorm.friendchise.domain.notification.application;

import com.goorm.friendchise.domain.notification.domain.Notification;
import com.goorm.friendchise.domain.notification.domain.NotificationRepository;
import com.goorm.friendchise.domain.notification.domain.NotificationTarget;
import com.goorm.friendchise.domain.notification.presentation.dto.NotificationCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
	private final NotificationRepository repository;

	// 알림 생성
	public Notification createNotification(NotificationCreateRequest request) {
		return repository.save(
			Notification.create(
				request.targetId(),
				request.targetType(),
				request.notificationType(),
				request.title(),
				request.content()
			)
		);
	}

	// 특정 대상의 알림 목록 조회
	@Transactional(readOnly = true)
	public List<Notification> getNotificationsByTarget(NotificationTarget targetType, Long targetId) {
		return repository.findByTargetTypeAndTargetId(targetType, targetId);
	}

	// 알림 읽음 처리
	public void markAsRead(Long notificationId) {
		Notification notification = repository.findById(notificationId)
			.orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
		notification.markAsRead();
	}

	// 알림 삭제
	public void deleteNotification(Long notificationId) {
		repository.delete(notificationId);
	}
}
