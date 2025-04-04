package com.goorm.api.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goorm.api.auth.domain.UserPrincipal;
import com.goorm.api.auth.dto.response.TokenResponse;
import com.goorm.api.auth.implement.jwt.TokenProvider;
import com.goorm.api.common.event.RefreshTokenSaveEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private final ApplicationEventPublisher eventPublisher;

    // 로그인 성공 시 호출
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(),
                TokenResponse.of(accessToken, refreshToken));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        eventPublisher.publishEvent(RefreshTokenSaveEvent.create(refreshToken, principal.getUsername(), principal.getRole()));
    }
}
