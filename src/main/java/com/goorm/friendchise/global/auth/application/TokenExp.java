package com.goorm.friendchise.global.auth.application;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum TokenExp {
    ACCESS_TOKEN_EXP(Duration.ofHours(1)),
    REFRESH_TOKEN_EXP(Duration.ofDays(1));

    private final Duration exp;

    TokenExp(Duration exp) {
        this.exp = exp;
    }
}
