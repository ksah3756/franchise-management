package com.goorm.friendchise.domain.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationDetailResponse(
	@Schema(description = "알림 ID", example = "1")
	Long id,

	@Schema(description = "대상 ID (수신자)", example = "101")
	Long targetId,

	@Schema(description = "알림 제목", example = "깜짝 할인")
	String title,

	@Schema(description = "알림 내용", example = "치킨 50% 할인")
	String content,

	@Schema(description = "알림 생성일", example = "2025-02-07T12:00:00")
	LocalDateTime createdAt,

	@Schema(description = "읽음 여부", example = "false")
	boolean isRead
) {
}

