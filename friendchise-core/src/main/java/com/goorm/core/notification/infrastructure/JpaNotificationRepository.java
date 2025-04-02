package com.goorm.core.notification.infrastructure;


import com.goorm.core.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaNotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByStoreId(Long storeId);
}
