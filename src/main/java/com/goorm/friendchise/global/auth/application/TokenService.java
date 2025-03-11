package com.goorm.friendchise.global.auth.application;

import com.goorm.friendchise.domain.manager.domain.Role;
import com.goorm.friendchise.domain.manager.exception.TokenNotFoundException;
import com.goorm.friendchise.global.auth.domain.RefreshToken;
import com.goorm.friendchise.global.auth.domain.RefreshTokenRepository;
import com.goorm.friendchise.global.auth.dto.request.TokenReissueRequest;
import com.goorm.friendchise.global.auth.dto.response.TokenResponse;
import com.goorm.friendchise.global.auth.implement.jwt.RefreshTokenIssuer;
import com.goorm.friendchise.global.auth.implement.jwt.TokenParser;
import com.goorm.friendchise.global.auth.implement.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.goorm.friendchise.global.auth.application.TokenExp.ACCESS_TOKEN_EXP;
import static com.goorm.friendchise.global.auth.application.TokenExp.REFRESH_TOKEN_EXP;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;
    private final TokenParser tokenParser;
    private final RefreshTokenIssuer refreshTokenIssuer;

    public String generateAccessToken(String username, String role) {
        return tokenProvider.generateToken(username, ACCESS_TOKEN_EXP.getExp(), role);
    }

    public String generateRefreshToken(String username, String role) {
        return tokenProvider.generateToken(username, REFRESH_TOKEN_EXP.getExp(), role);
    }

    public TokenResponse reissue(TokenReissueRequest request) {
        String inputRefreshToken = request.refreshToken();

        String username = tokenParser.getUsername(inputRefreshToken);

        RefreshToken savedRefreshToken  = refreshTokenIssuer.getRefreshToken(inputRefreshToken);
        Role role = savedRefreshToken.getRole();
        String roleName = role.name();

        String accessToken = tokenProvider.generateToken(username, ACCESS_TOKEN_EXP.getExp(), roleName);
        String refreshToken = tokenProvider.generateToken(username, REFRESH_TOKEN_EXP.getExp(), roleName);

        refreshTokenIssuer.saveRefreshToken(refreshToken, savedRefreshToken.getId(), role);

        return TokenResponse.of(accessToken, refreshToken);
    }
}
