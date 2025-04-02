package com.goorm.core.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	HEADQUARTER("본사"),
	STORE("매장"),
	CUSTOMER("고객");
	private final String description;
}
