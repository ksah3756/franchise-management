package com.goorm.api.auth.domain;

import com.goorm.core.user.domain.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24) // TODO: secondary Index, key 목록 set 도 제대로 삭제되는지 확인 필요
public class RefreshToken implements Serializable {
	@Indexed
	private String refreshToken;

	@Id
	private String id;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	public static RefreshToken of(String refreshToken, String id, UserRole userRole) {
		return RefreshToken.builder()
			.refreshToken(refreshToken)
			.id(id)
			.userRole(userRole)
			.build();
	}
}
