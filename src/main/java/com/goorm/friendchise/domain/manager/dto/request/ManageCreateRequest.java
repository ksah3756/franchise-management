package com.goorm.friendchise.domain.manager.dto.request;

import com.goorm.friendchise.domain.manager.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record ManageCreateRequest(
	@Schema(description = "로그인아이디", example = "organking", requiredMode = REQUIRED)
	@NotNull
	String username,

	@Schema(description = "비밀번호", example = "organking1234", requiredMode = REQUIRED)
	@NotNull
	String password,

	@Schema(description = "역할", example = "HEADQUARTER", requiredMode = REQUIRED)
	@NotNull
	Role role,

	@Schema(description = "본사ID", example = "1")
	Long headquarterId,

	@Schema(description = "인증번호", example = "12345678")
	String certificationNumber
) {
}
