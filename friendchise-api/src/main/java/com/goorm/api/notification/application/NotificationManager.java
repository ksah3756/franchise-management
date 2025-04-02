package com.goorm.api.notification.application;

import com.goorm.api.auth.implement.jwt.TokenParser;
import com.goorm.api.headquarter.dto.store.StoreIdDto;
import com.goorm.api.notification.dto.response.ReceivedNotificationResponse;
import com.goorm.api.notification.event.NotificationDeletedEvent;
import com.goorm.api.notification.event.NotificationReadEvent;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.notification.domain.Notification;
import com.goorm.core.notification.domain.NotificationRepository;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Manager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationManager {
	private final NotificationRepository repository;
	private final ApplicationEventPublisher eventPublisher;
	private final TokenParser tokenParser;

	protected void verifyRole(String token) {
		String role = tokenParser.getStoreRole(token);
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
	public void markAsRead(User user, Long notificationId) {
		Long storeId = user.getId();

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
	public void deleteNotification(User user, Long notificationId) {
		repository.deleteById(notificationId);
		log.info("Notification {} deleted", notificationId);
		eventPublisher.publishEvent(new NotificationDeletedEvent(notificationId));
	}

	@Transactional
	public List<ReceivedNotificationResponse> getNotifications(User user) {
		Long storeId = user.getId();

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
