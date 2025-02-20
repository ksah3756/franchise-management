package com.goorm.friendchise.global.auth.jwt;

import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import static io.jsonwebtoken.Header.JWT_TYPE;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static javax.xml.crypto.dsig.SignatureProperties.TYPE;

@Service
@Builder
@RequiredArgsConstructor
public class TokenProvider {
	private final JwtProperties jwtProperties;

	public String generateToken(String username, Duration expiredAt, String role) {
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

	public String generateToken(String username, Duration expiredAt, String role, Long storeId) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expiredAt.toMillis());
		return makeToken(now, expiry, username, role, storeId);
	}

	private String makeToken(Date now, Date expiry, String username, String role, Long storeId) {
		return Jwts.builder()
			.setHeaderParam(TYPE, JWT_TYPE)
			.setIssuer(jwtProperties.getIssuer())
			.setIssuedAt(now)
			.setExpiration(expiry)
			.setSubject(username)
			.claim("username", username)
			.claim("role", role)
			.claim("storeId", storeId)
			.signWith(HS256, jwtProperties.getSecretKey())
			.compact();
	}

	public String generateToken(String username, Duration expiredAt, String role,
								Long userId, String category, String subCategory) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expiredAt.toMillis());
		return makeToken(now, expiry, username, role, userId, category, subCategory);
	}

	private String makeToken(Date now, Date expiry, String username, String role,
							 Long userId, String category, String subCategory) {
		return Jwts.builder()
			.setHeaderParam(TYPE, JWT_TYPE)
			.setIssuer(jwtProperties.getIssuer())
			.setIssuedAt(now)
			.setExpiration(expiry)
			.setSubject(username)
			.claim("username", username)
			.claim("role", role)
			.claim("userId", userId.toString())
			.claim("category", category)
			.claim("subCategory", subCategory)
			.signWith(HS256, jwtProperties.getSecretKey())
			.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.setSigningKey(jwtProperties.getSecretKey())
				.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);
		String role = claims.get("role", String.class);
		Set<SimpleGrantedAuthority> authorities = getRoles(role);

		return new UsernamePasswordAuthenticationToken(
			new org.springframework.security.core.userdetails.User(
				claims.getSubject(),
				"",
				authorities
			), token, authorities
		);
	}

	public Set<SimpleGrantedAuthority> getRoles(String role) {
		if (role.equals("HEADQUARTER")) {
			return Collections.singleton(new SimpleGrantedAuthority("ROLE_HEADQUARTER"));
		}
		if (role.equals("STORE")) {
			return Collections.singleton(new SimpleGrantedAuthority("ROLE_STORE"));
		}
		return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
	}

	public String getUsername(String token) {
		Claims claims = getClaims(token);
		return claims.get("username", String.class);
	}

	public String getHeadquarterManagerId(String token) {
		Claims claims = getClaims(token);
		return claims.get("userId", String.class);
	}

	public String getHeadquarterCategory(String token) {
		Claims claims = getClaims(token);
		return claims.get("category", String.class);
	}

	public String getHeadquarterSubCategory(String token) {
		Claims claims = getClaims(token);
		return claims.get("subCategory", String.class);
	}

	public String getStoreRole(String token) {
		Claims claims = getClaims(token);
		return claims.get("role", String.class);
	}

	private Claims getClaims(String token) {
		return Jwts.parser()
			.setSigningKey(jwtProperties.getSecretKey())
			.parseClaimsJws(token)
			.getBody();
	}
}
