package com.goorm.api.store.exception;


import com.goorm.core.common.exception.CustomException;

import static com.goorm.core.common.exception.ErrorCode.STORE_NOT_FOUND;


public class StoreNotFoundException extends CustomException {
    public StoreNotFoundException() {
        super(STORE_NOT_FOUND);
    }
}
