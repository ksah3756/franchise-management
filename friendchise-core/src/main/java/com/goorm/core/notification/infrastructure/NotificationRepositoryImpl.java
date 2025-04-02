package com.goorm.core.notification.infrastructure;

import com.goorm.core.notification.domain.Notification;
import com.goorm.core.notification.domain.NotificationRepository;
import com.goorm.core.notification.infrastructure.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {
	private final JpaNotificationRepository jpaNotificationRepository;

	@Override
	public List<Notification> findAll() {
		return jpaNotificationRepository.findAll();
	}

	@Override
	public List<Notification> findByStoreId(Long storeId) {
		return jpaNotificationRepository.findByStoreId(storeId);
	}

	@Override
	public Notification save(Notification notification) {
		return jpaNotificationRepository.save(notification);
	}

	@Override
	public List<Notification> saveAll(List<Notification> notifications) {
		return jpaNotificationRepository.saveAll(notifications);
	}

	@Override
	public Optional<Notification> findById(Long id) {
		return jpaNotificationRepository.findById(id);
	}

	// 알림 객체 삭제로 개선 필요
	@Override
	public void deleteById(Long id) {
		jpaNotificationRepository.deleteById(id);
	}
}
