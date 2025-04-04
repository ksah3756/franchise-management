package com.goorm.api.store.exception;


import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;

public class NoAuthenticationException extends CustomException {
    public NoAuthenticationException() {
        super(ErrorCode.NOT_VALID_AUTHENTICATION);
    }
}
