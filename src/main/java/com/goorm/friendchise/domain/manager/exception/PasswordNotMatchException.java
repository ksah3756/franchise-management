package com.goorm.friendchise.domain.manager.exception;

import com.goorm.friendchise.global.exception.CustomException;

import static com.goorm.friendchise.global.exception.ErrorCode.PASSWORD_NOT_MATCH;

public class PasswordNotMatchException extends CustomException {
	public PasswordNotMatchException() {
		super(PASSWORD_NOT_MATCH);
	}
}
