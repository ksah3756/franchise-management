package com.goorm.friendchise.global.auth.implement.jwt;

import com.goorm.friendchise.domain.manager.domain.Role;
import com.goorm.friendchise.domain.manager.exception.TokenNotFoundException;
import com.goorm.friendchise.global.auth.domain.RefreshToken;
import com.goorm.friendchise.global.auth.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenIssuer {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken getRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(TokenNotFoundException::new);
    }

    public void saveRefreshToken(String refreshToken, Long id, Role role) {
        refreshTokenRepository.save(RefreshToken.of(refreshToken, id, role));
    }
}
