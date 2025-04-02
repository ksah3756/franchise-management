package com.goorm.api.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record TokenReissueRequest(
	@Schema(description = "리프레시 토큰", example = "token-header.payload.signature", requiredMode = REQUIRED)
	@NotNull
	String refreshToken
) {
}
