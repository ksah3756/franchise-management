package com.goorm.friendchise.domain.notification.application;

import com.goorm.friendchise.domain.headquarter.business.HeadquarterService;
import com.goorm.friendchise.domain.headquarter.dto.store.StoreIdDto;
import com.goorm.friendchise.domain.notification.domain.Notification;
import com.goorm.friendchise.domain.notification.event.PromotionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventHandler {
	private final HeadquarterService headquarterService;
	private final NotificationManager notificationManager;
	private final NotificationSseSender notificationSseSender;

	@EventListener
	public void handlePromotionCreated(PromotionCreatedEvent event) {
		log.info("프로모션 생성 이벤트 감지: {}", event);
		List<Notification> notifications = processPromotionNotification(event);
		sendNotifications(notifications);
	}

//	@EventListener
//	public void handleNotificationRead(NotificationReadEvent event) {
//		log.info("알림 읽음 이벤트 감지: 알림ID={}", event.getNotification().getId());
//	}
//
//	@EventListener
//	public void handleNotificationDeleted(NotificationDeletedEvent event) {
//		log.info("알림 삭제 이벤트 감지: 알림ID={}", event.getNotificationId());
//	}

	private List<Notification> processPromotionNotification(PromotionCreatedEvent event) {
		Long headquarterId = event.getPromotion().getHeadquarterId();
		String title = event.getPromotion().getTitle();
		String content = event.getPromotion().getContent();

		List<StoreIdDto> storeIds = headquarterService.getStoreIdList(headquarterId);
		return notificationManager.createNotifications(storeIds, title, content);
	}

	private void sendNotifications(List<Notification> notifications) {
		notifications.forEach(notification ->
			notificationSseSender.sendSse(notification.getStoreId(), notification.getTitle(), notification.getContent(), notification.getId())
		);
	}
}
