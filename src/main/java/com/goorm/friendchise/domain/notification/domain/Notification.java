package com.goorm.friendchise.domain.notification.domain;

import com.goorm.friendchise.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Notification extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long targetId; // 매장 ID (무조건 STORE)

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 255)
	private String content;

	@Column(nullable = false)
	private boolean isRead;

	public static Notification create(Long storeId, String title, String content) {
		return Notification.builder()
			.targetId(storeId)
			.title(title)
			.content(content)
			.isRead(false)
			.build();
	}

	public void markAsRead() {
		this.isRead = true;
	}
}
