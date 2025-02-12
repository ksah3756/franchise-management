package com.goorm.friendchise.domain.manager.dto.request;

import com.goorm.friendchise.domain.manager.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Builder
public record ManageCreateRequest(
	@Schema(description = "로그인아이디", example = "organking", requiredMode = REQUIRED)
	@NotBlank @Size(min = 1, max = 50, message = "로그인아이디는 50자 이내여야 합니다.")
	String username,

	@Schema(description = "비밀번호", example = "organking1234", requiredMode = REQUIRED)
	@NotBlank @Size(min = 4, max = 20, message = "비밀번호는 4자 이상, 20자 이내여야 합니다.")
	String password,

	@Schema(description = "역할", example = "HEADQUARTER", requiredMode = REQUIRED)
	@NotBlank
	Role role,

	@Schema(description = "본사ID", example = "1")
	Long headquarterId,

	@Schema(description = "인증번호", example = "12345678")
	String certificationNumber
) {
}
