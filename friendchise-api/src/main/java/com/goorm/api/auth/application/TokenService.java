package com.goorm.api.auth.application;

import com.goorm.api.auth.domain.RefreshToken;
import com.goorm.api.auth.dto.request.TokenReissueRequest;
import com.goorm.api.auth.dto.response.TokenResponse;
import com.goorm.api.auth.implement.jwt.RefreshTokenIssuer;
import com.goorm.api.auth.implement.jwt.TokenParser;
import com.goorm.api.auth.implement.jwt.TokenProvider;
import com.goorm.core.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final TokenParser tokenParser;
    private final RefreshTokenIssuer refreshTokenIssuer;

    public TokenResponse reissue(TokenReissueRequest request) {
        String inputRefreshToken = request.refreshToken();
        Authentication authentication = tokenParser.getAuthentication(inputRefreshToken);

        RefreshToken savedRefreshToken  = refreshTokenIssuer.getRefreshToken(inputRefreshToken);
        UserRole userRole = savedRefreshToken.getUserRole();

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        refreshTokenIssuer.saveRefreshToken(refreshToken, savedRefreshToken.getId(), userRole);

        return TokenResponse.of(accessToken, refreshToken);
    }
}
