package com.goorm.api.store.exception;

import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;

public class SalesNotFoundException extends CustomException {
    public SalesNotFoundException() {
        super(ErrorCode.SALES_NOT_FOUND);
    }
}
