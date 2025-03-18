package com.goorm.friendchise.global.auth.infrastructure;

import com.goorm.friendchise.global.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisRefreshTokenRepository extends CrudRepository<RefreshToken, String> {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	void deleteByRefreshToken(String refreshToken);
}
