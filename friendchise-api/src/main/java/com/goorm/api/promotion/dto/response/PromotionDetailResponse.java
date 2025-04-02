package com.goorm.api.promotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PromotionDetailResponse(
	@Schema(description = "프로모션 ID", example = "1")
	Long id,

	@Schema(description = "프로모션 제목", example = "봄맞이 할인")
	String title,

	@Schema(description = "프로모션 내용", example = "전 제품 30% 할인!")
	String content,

	@Schema(description = "시작일", example = "2025-03-01T09:00:00")
	LocalDateTime startDate,

	@Schema(description = "종료일", example = "2025-03-07T23:59:59")
	LocalDateTime endDate
) {}