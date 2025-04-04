package com.goorm.api.user.dto.response;

import lombok.Builder;

@Builder
public record UserPersistResponse(
	Long id
) {
	public static UserPersistResponse of(Long id) {
		return UserPersistResponse.builder().id(id).build();
	}
}
