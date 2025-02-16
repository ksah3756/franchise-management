package com.goorm.friendchise.domain.notification.application;

import com.goorm.friendchise.domain.headquarter.application.HeadquarterService;
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
	public void handlePromotionCreated(PromotionCreatedEvent promotion) {
		log.info("프로모션 생성 이벤트 감지 완료: {}", promotion);
		List<Notification> notifications = processPromotionNotification(promotion);
		sendNotifications(notifications);
	}

	private List<Notification> processPromotionNotification(PromotionCreatedEvent promotion) {
		Long headquarterId = promotion.getPromotion().getHeadquarterId();
		String title = promotion.getPromotion().getTitle();
		String content = promotion.getPromotion().getContent();

		List<StoreIdDto> storeIds = headquarterService.getStoreIdList(headquarterId);
		return notificationManager.createNotifications(storeIds, title, content);
	}

	private void sendNotifications(List<Notification> notifications) {
		notifications.forEach(notification ->
			notificationSseSender.sendSse(notification.getStoreId(), notification.getTitle(), notification.getContent())
		);
	}
}
