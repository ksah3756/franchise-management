package com.goorm.friendchise.domain.notification.application;

import com.goorm.friendchise.domain.headquarter.dto.store.StoreIdDto;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.Role;
import com.goorm.friendchise.domain.notification.domain.Notification;
import com.goorm.friendchise.domain.notification.domain.NotificationRepository;
import com.goorm.friendchise.domain.notification.dto.response.ReceivedNotificationResponse;
import com.goorm.friendchise.domain.notification.event.NotificationDeletedEvent;
import com.goorm.friendchise.domain.notification.event.NotificationReadEvent;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.jwt.TokenProvider;
import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationManager {
	private final NotificationRepository repository;
	private final AuthService authService;
	private final ApplicationEventPublisher eventPublisher;
	private final TokenProvider tokenProvider;

	private Manager getAuthStoreManager() {
		Manager manager = authService.findManagerByAuth();
		if (manager.getRole() != Role.STORE) {
			throw new CustomException(ErrorCode.NO_STORE_AUTHENTICATION_ERROR);
		}
		return manager;
	}

	protected void verifyRole(String token) {
		String role = tokenProvider.getStoreRole(token);
		if (!role.equals("STORE")) {
			throw new CustomException(ErrorCode.NO_STORE_AUTHENTICATION_ERROR);
		}
	}

	public List<Notification> createNotifications(List<StoreIdDto> storeIds, String title, String content) {
		List<Notification> notifications = storeIds.stream()
			.map(storeId -> Notification.create(storeId.id(), title, content))
			.collect(Collectors.toList());
		saveAllNotification(notifications);
		return notifications;
	}

	@Transactional
	public void saveAllNotification(List<Notification> notifications) {
		repository.saveAll(notifications);
		log.info("Saved {} notifications", notifications.size());
	}

	@Transactional
	public void markAsRead(Long notificationId) {
		Manager storeManager = getAuthStoreManager();
		Long storeId = storeManager.getManageId();

		Notification notification = repository.findById(notificationId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

		if (!notification.getStoreId().equals(storeId)) {
			throw new CustomException(ErrorCode.NO_STORE_EQUAL_AUTHENTICATION_ERROR);
		}

		notification.markAsRead();
		log.info("Notification {} marked as read", notificationId);
		eventPublisher.publishEvent(new NotificationReadEvent(notification));
	}

	@Transactional
	public void deleteNotification(Long notificationId) {
		repository.deleteById(notificationId);
		log.info("Notification {} deleted", notificationId);
		eventPublisher.publishEvent(new NotificationDeletedEvent(notificationId));
	}

	@Transactional
	public List<ReceivedNotificationResponse> getNotifications() {
		Manager storeManager = getAuthStoreManager();
		Long storeId = storeManager.getManageId();

		return repository.findByStoreId(storeId)
			.stream()
			.map(this::toReceivedNotificationResponse)
			.collect(Collectors.toList());
	}

	private ReceivedNotificationResponse toReceivedNotificationResponse(Notification notification) {
		return ReceivedNotificationResponse.builder()
			.title(notification.getTitle())
			.content(notification.getContent())
			.isRead(notification.isRead())
			.build();
	}
}
