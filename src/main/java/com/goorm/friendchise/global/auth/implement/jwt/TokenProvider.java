package com.goorm.friendchise.global.auth.implement.jwt;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Date;

import static com.goorm.friendchise.global.auth.application.TokenExp.ACCESS_TOKEN_EXP;
import static com.goorm.friendchise.global.auth.application.TokenExp.REFRESH_TOKEN_EXP;
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

	public String generateAccessToken(String username, String role) {
		return generateToken(username, ACCESS_TOKEN_EXP.getExp(), role);
	}

	public String generateRefreshToken(String username, String role) {
		return generateToken(username, REFRESH_TOKEN_EXP.getExp(), role);
	}
}
