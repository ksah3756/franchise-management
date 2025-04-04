package com.goorm.core.notification.domain;

import com.goorm.core.notification.domain.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
	List<Notification> findAll();

	List<Notification> findByStoreId(Long storeId);

	Notification save(Notification notification);

	List<Notification> saveAll(List<Notification> notifications);

	Optional<Notification> findById(Long id);

	void deleteById(Long id);
}
