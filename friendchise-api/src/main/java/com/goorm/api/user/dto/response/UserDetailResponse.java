package com.goorm.api.user.dto.response;

import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRole;
import lombok.Builder;


@Builder
public record UserDetailResponse(
	Long id,
	String username,
	UserRole userRole,
	String certificationNumber
) {
	public static UserDetailResponse from(User user) {
		return UserDetailResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.userRole(user.getUserRole())
			.build();
	}

	public static UserDetailResponse fromHeadquarter(User user, String headquarterCertificationNumber) {
		return UserDetailResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.userRole(user.getUserRole())
			.certificationNumber(headquarterCertificationNumber)
			.build();
	}
}
