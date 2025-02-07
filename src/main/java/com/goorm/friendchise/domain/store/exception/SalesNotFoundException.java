package com.goorm.friendchise.domain.store.exception;

import com.goorm.friendchise.global.exception.CustomException;
import com.goorm.friendchise.global.exception.ErrorCode;

public class SalesNotFoundException extends CustomException {
    public SalesNotFoundException() {
        super(ErrorCode.SALES_NOT_FOUND);
    }
}
