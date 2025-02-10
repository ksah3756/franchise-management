package com.goorm.friendchise.domain.notification.infrastructure;

import com.goorm.friendchise.domain.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaNotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByTargetId(Long targetId);
}
