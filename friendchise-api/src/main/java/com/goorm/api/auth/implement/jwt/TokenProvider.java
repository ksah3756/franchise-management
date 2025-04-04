package com.goorm.api.auth.implement.jwt;

import com.goorm.api.auth.application.TokenExp;
import com.goorm.api.auth.domain.UserPrincipal;
import io.jsonwebtoken.Jwts;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

import static com.goorm.api.auth.application.TokenExp.ACCESS_TOKEN_EXP;
import static com.goorm.api.auth.application.TokenExp.REFRESH_TOKEN_EXP;
import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static javax.xml.crypto.dsig.SignatureProperties.TYPE;

@Service
@Builder
@RequiredArgsConstructor
public class TokenProvider {
	private final JwtProperties jwtProperties;

	private String generateToken(String username, Duration expiredAt, String role) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expiredAt.toMillis());
		return makeToken(now, expiry, username, role);
	}

	private String makeToken(Date now, Date expiry, String username, String role) {
		return Jwts.builder()
			.setHeaderParam(TYPE, JWT_TYPE)
			.setIssuer(jwtProperties.getIssuer())
			.setIssuedAt(now)
			.setExpiration(expiry)
			.setSubject(username)
			.claim("username", username)
			.claim("role", role)
			.signWith(HS256, jwtProperties.getSecretKey())
			.compact();
	}

	public String generateAccessToken(Authentication authentication) {
		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		return generateToken(principal.getUsername(), ACCESS_TOKEN_EXP.getExp(), principal.getRole().name());
	}

	public String generateRefreshToken(Authentication authentication) {
		UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
		return generateToken(principal.getUsername(), REFRESH_TOKEN_EXP.getExp(), principal.getRole().name());
	}
}
