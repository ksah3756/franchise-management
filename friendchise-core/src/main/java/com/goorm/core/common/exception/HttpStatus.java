package com.goorm.core.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum HttpStatus {
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    INTERNAL_SERVER_ERROR(500);

    private final int value;
}
