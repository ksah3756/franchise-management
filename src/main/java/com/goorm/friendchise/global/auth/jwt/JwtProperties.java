package com.goorm.friendchise.global.auth.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class JwtProperties {
	@Value("${jwt.issuer}")
	private String issuer;
	@Value("${jwt.secret}")
	private String secretKey;
}
