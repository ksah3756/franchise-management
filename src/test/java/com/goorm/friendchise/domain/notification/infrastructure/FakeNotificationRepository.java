package com.goorm.friendchise.domain.notification.infrastructure;

import com.goorm.friendchise.domain.notification.domain.Notification;
import com.goorm.friendchise.domain.notification.domain.NotificationRepository;
import com.goorm.friendchise.domain.notification.domain.NotificationTarget;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakeNotificationRepository implements NotificationRepository {
	private final List<Notification> notifications = Collections.synchronizedList(new ArrayList<>());
	private final AtomicLong sequence = new AtomicLong(1);

	@Override
	public Notification save(Notification notification) {
		Notification savedNotification = new Notification(
			sequence.getAndIncrement(),
			notification.getTargetId(),
			notification.getTargetType(),
			notification.getNotificationType(),
			notification.getTitle(),
			notification.getContent(),
			false
		);
		notifications.add(savedNotification);
		return savedNotification;
	}

	@Override
	public Optional<Notification> findById(Long id) {
		return notifications.stream()
			.filter(notification -> notification.getId().equals(id))
			.findFirst();
	}

	@Override
	public List<Notification> findByTargetTypeAndTargetId(NotificationTarget targetType, Long targetId) {
		return notifications.stream()
			.filter(notification -> notification.getTargetType() == targetType && notification.getTargetId().equals(targetId))
			.collect(Collectors.toList());
	}

	@Override
	public void delete(Long id) {
		notifications.removeIf(n -> n.getId().equals(id));
	}
}
