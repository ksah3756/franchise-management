package com.goorm.friendchise.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메소드입니다."),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "유효하지 않은 파라미터입니다."),
	TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "타입이 일치하지 않습니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리소스를 찾을 수 없습니다."),
	URL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 URL을 찾을 수 없습니다."),

	// Auth Error
	DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST,"중복된 아이디입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"유효하지 않은 비밀번호입니다."),
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED,"아이디 또는 비밀번호가 틀립니다."),
	PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
	HEADQUARTER_AUTH_NOT_MATCH(HttpStatus.BAD_REQUEST, "본사 인증번호가 일치하지 않습니다."),
	TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않거나 만료된 토큰입니다."),

	// Headquarter Error
	FRANCHISE_NAME_DUPLICATION(HttpStatus.BAD_REQUEST, "이미 존재하는 프랜차이즈 이름입니다."),
	HEADQUARTER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 프랜차이즈 본사입니다."),
    FRANCHISE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 프랜차이즈입니다."),
	FRANCHISE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상위 카테고리입니다."),
	FRANCHISE_SUBCATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 하위 카테고리입니다."),
	// STORE
	STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 매장을 찾을 수 없습니다."),
	SALES_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 매출을 찾을 수 없습니다."),
	NOT_FOUND_ADDRESS(HttpStatus.BAD_REQUEST, "해당 주소와 관련된 주소를 찾을 수 없습니다."),

	// WebClient
	WEBCLIENT_ERROR(HttpStatus.BAD_REQUEST, "API 호출 도중 에러가 발생했습니다.");

	private final HttpStatus status;
	private final String message;

}
