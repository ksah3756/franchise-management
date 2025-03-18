package com.goorm.friendchise.global.auth.implement.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TokenParser {
    private final JwtProperties jwtProperties;

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
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

    public String getStoreRole(String token) {
        Claims claims = getClaims(token);
        return claims.get("role", String.class);
    }
}
