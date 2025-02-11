package com.goorm.friendchise.domain.manager.exception;

import com.goorm.friendchise.global.exception.CustomException;

import static com.goorm.friendchise.global.exception.ErrorCode.HEADQUARTER_AUTH_NOT_MATCH;

public class HeadquarterAuthNotMatchException extends CustomException {
	public HeadquarterAuthNotMatchException() {
		super(HEADQUARTER_AUTH_NOT_MATCH);
	}
}
