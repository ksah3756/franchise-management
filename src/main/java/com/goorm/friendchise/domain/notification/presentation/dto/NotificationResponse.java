package com.goorm.friendchise.domain.notification.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record NotificationResponse(
	@Schema(description = "알림 ID", example = "1")
	Long id,

	@Schema(description = "상태", example = "success")
	String status,

	@Schema(description = "응답 메시지", example = "알림이 정상적으로 전송되었습니다.")
	String message
) {
}
