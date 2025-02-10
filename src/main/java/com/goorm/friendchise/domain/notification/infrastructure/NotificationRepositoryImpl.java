package com.goorm.friendchise.domain.notification.infrastructure;

import com.goorm.friendchise.domain.notification.domain.Notification;
import com.goorm.friendchise.domain.notification.domain.NotificationRepository;
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
	public List<Notification> findByTargetId(Long targetId) {
		return jpaNotificationRepository.findByTargetId(targetId);
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
	public void delete(Long id) {
		jpaNotificationRepository.deleteById(id);
	}
}
