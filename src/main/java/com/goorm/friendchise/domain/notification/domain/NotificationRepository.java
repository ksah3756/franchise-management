package com.goorm.friendchise.domain.notification.domain;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
	List<Notification> findAll();

	List<Notification> findByTargetId(Long targetId);

	Notification save(Notification notification);

	List<Notification> saveAll(List<Notification> notifications);

	Optional<Notification> findById(Long id);

	void delete(Long id);
}
