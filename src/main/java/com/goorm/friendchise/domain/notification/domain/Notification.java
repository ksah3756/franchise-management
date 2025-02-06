package com.goorm.friendchise.domain.notification.domain;

import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationTarget targetType;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 255)
	private String content;

	@Column(nullable = false)
	private boolean isRead;

	public static Notification create(Long targetId, NotificationTarget targetType, NotificationType notificationType, String title, String content) {
		return Notification.builder()
			.targetId(targetId)
			.targetType(targetType)
			.notificationType(notificationType)
			.title(title)
			.content(content)
			.isRead(false)
			.build();
	}

	public void markAsRead() {
		this.isRead = true;
	}
}
