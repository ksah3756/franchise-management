package com.goorm.friendchise.domain.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReceivedNotificationResponse(
	@Schema(description = "알림 제목", example = "깜짝 할인")
	String title,

	@Schema(description = "알림 내용", example = "치킨 50% 할인")
	String content
) {
}
