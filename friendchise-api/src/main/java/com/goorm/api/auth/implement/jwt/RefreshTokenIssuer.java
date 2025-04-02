package com.goorm.api.auth.implement.jwt;

import com.goorm.api.auth.domain.RefreshToken;
import com.goorm.api.auth.domain.RefreshTokenRepository;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenIssuer {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken getRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));
    }

    public void saveRefreshToken(String refreshToken, String id, UserRole userRole) {
        refreshTokenRepository.save(RefreshToken.of(refreshToken, id, userRole));
    }
}
