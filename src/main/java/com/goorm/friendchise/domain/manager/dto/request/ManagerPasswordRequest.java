package com.goorm.friendchise.domain.manager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record ManagerPasswordRequest(
	@Schema(description = "새 비밀번호", example = "organking1234", requiredMode = REQUIRED)
	@NotBlank @Size(min = 4, max = 20, message = "비밀번호는 4자 이상, 20자 이내여야 합니다.")
	String password
) {
}
