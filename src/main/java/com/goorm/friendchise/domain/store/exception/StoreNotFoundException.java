package com.goorm.friendchise.domain.store.exception;

import com.goorm.friendchise.global.exception.CustomException;

import static com.goorm.friendchise.global.exception.ErrorCode.STORE_NOT_FOUND;

public class StoreNotFoundException extends CustomException {
    public StoreNotFoundException() {
        super(STORE_NOT_FOUND);
    }
}
