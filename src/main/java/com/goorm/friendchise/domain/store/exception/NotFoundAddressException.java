package com.goorm.friendchise.domain.store.exception;

import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;

public class NotFoundAddressException extends CustomException {
    public NotFoundAddressException() {
        super(ErrorCode.NOT_FOUND_ADDRESS);
    }
}
