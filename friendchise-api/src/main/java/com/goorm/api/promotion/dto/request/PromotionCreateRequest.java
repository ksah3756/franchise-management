package com.goorm.api.promotion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record PromotionCreateRequest(
	@Schema(description = "프로모션 이름", example = "깜짝 할인", requiredMode = REQUIRED)
	@NotNull
	String title,

	@Schema(description = "프로모션 내용", example = "치킨 50% 할인", requiredMode = REQUIRED)
	@NotNull
	String content,

	@Schema(description = "시작 기간", example = "2025-02-10T09:00:00", requiredMode = REQUIRED)
	@NotNull
	LocalDateTime startDate,

	@Schema(description = "종료 기간", example = "2025-02-10T23:59:59", requiredMode = REQUIRED)
	@NotNull
	LocalDateTime endDate
) {
}
