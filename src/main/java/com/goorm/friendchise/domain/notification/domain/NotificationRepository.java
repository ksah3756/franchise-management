package com.goorm.friendchise.domain.notification.domain;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
	Notification save(Notification notification);

	Optional<Notification> findById(Long id);

	void delete(Long id);

	List<Notification> findByTargetTypeAndTargetId(NotificationTarget targetType, Long targetId);
}
