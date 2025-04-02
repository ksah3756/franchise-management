package com.goorm.api.store.exception;


import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;

public class NotFoundAddressException extends CustomException {
    public NotFoundAddressException() {
        super(ErrorCode.NOT_FOUND_ADDRESS);
    }
}
