package com.goorm.friendchise.domain.notification.presentation.dto;

import com.goorm.friendchise.domain.notification.domain.NotificationTarget;
import com.goorm.friendchise.domain.notification.domain.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record NotificationCreateRequest(
	@Schema(description = "알림 대상 ID (매장 or 고객)", example = "101", requiredMode = REQUIRED)
	@NotNull
	Long targetId,

	@Schema(description = "알림 대상 타입 (매장 or 고객)", example = "STORE", requiredMode = REQUIRED)
	@NotNull
	NotificationTarget targetType,

	@Schema(description = "알림 유형", example = "PROMOTION", requiredMode = REQUIRED)
	@NotNull
	NotificationType notificationType,

	@Schema(description = "알림 제목", example = "한정 프로모션", requiredMode = REQUIRED)
	@NotNull
	String title,

	@Schema(description = "알림 내용", example = "이번 주 한정 20% 할인 이벤트", requiredMode = REQUIRED)
	@NotNull
	String content
) {
}
